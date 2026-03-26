package com.example.zhjypt.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.zhjypt.mapper.QuestionMapper;
import com.example.zhjypt.mapper.StudentAnswerMapper;
import com.example.zhjypt.mapper.StudentExamRecordMapper;
import com.example.zhjypt.pojo.Question;
import com.example.zhjypt.pojo.StudentAnswer;
import com.example.zhjypt.pojo.StudentExamRecord;
import com.example.zhjypt.service.StudentAnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 学生答题记录表 服务实现类
 * </p>
 *
 * @author system
 * @since 2026-01-09
 */
@Service
public class StudentAnswerServiceImpl extends ServiceImpl<StudentAnswerMapper, StudentAnswer> implements StudentAnswerService {

    @Autowired
    private StudentAnswerMapper studentAnswerMapper;
    
    @Autowired
    private QuestionMapper questionMapper;
    
    @Autowired
    private StudentExamRecordMapper studentExamRecordMapper;

    @Override
    public boolean saveStudentAnswer(Integer recordId, Integer questionId, String studentAnswer, Integer questionScore) {
        try {
            // 检查是否已存在答案记录
            QueryWrapper<StudentAnswer> wrapper = new QueryWrapper<>();
            wrapper.eq("record_id", recordId).eq("question_id", questionId);
            StudentAnswer existingAnswer = getOne(wrapper);

            if (existingAnswer != null) {
                // 更新现有记录
                existingAnswer.setStudentAnswer(studentAnswer);
                existingAnswer.setAnswerTime(new Date());
                return updateById(existingAnswer);
            } else {
                // 获取教师ID（通过 record -> exam -> course -> teacher）
                StudentExamRecord record = studentExamRecordMapper.selectById(recordId);
                Integer teacherId = null;
                if (record != null) {
                    // 这里需要通过exam获取course，再获取teacher_id
                    // 暂时先不设置，后续通过SQL自动填充
                }
                
                // 创建新记录
                StudentAnswer answer = new StudentAnswer();
                answer.setRecordId(recordId);
                answer.setQuestionId(questionId);
                answer.setStudentAnswer(studentAnswer);
                answer.setQuestionScore(questionScore);
                answer.setAnswerTime(new Date());
                
                // 获取题目信息，判断是否为客观题
                Question question = questionMapper.selectById(questionId);
                if (question != null && question.getQuestionType() <= 3) {
                    // 客观题（1单选，2多选，3判断）自动评分
                    answer.setIsCorrect(checkObjectiveAnswer(question, studentAnswer));
                    if (answer.getIsCorrect() == 1) {
                        answer.setStudentScore(BigDecimal.valueOf(questionScore));
                    } else {
                        answer.setStudentScore(BigDecimal.ZERO);
                    }
                } else {
                    // 主观题（4填空，5简答）等待批改
                    answer.setIsCorrect(null);
                    answer.setStudentScore(null);
                }
                
                return save(answer);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean batchSaveStudentAnswers(Integer recordId, String answersJson) {
        try {
            JSONArray answersArray = JSON.parseArray(answersJson);
            
            for (int i = 0; i < answersArray.size(); i++) {
                JSONObject answerObj = answersArray.getJSONObject(i);
                Integer questionId = answerObj.getInteger("questionId");
                String studentAnswer = answerObj.getString("studentAnswer");
                Integer questionScore = answerObj.getInteger("questionScore");

                saveStudentAnswer(recordId, questionId, studentAnswer, questionScore);
            }
            
            // 保存完答案后，计算客观题总分并更新考试记录
            updateExamRecordScore(recordId);
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Map<String, Object>> getStudentAnswersWithQuestions(Integer recordId) {
        return studentAnswerMapper.selectAnswersByRecordId(recordId);
    }

    @Override
    public boolean gradeSubjectiveAnswers(Integer recordId, String gradingData) {
        try {
            System.out.println("=== 开始批改主观题 ===");
            System.out.println("recordId: " + recordId);
            System.out.println("gradingData: " + gradingData);
            
            JSONArray gradingArray = JSON.parseArray(gradingData);
            
            for (int i = 0; i < gradingArray.size(); i++) {
                JSONObject gradingObj = gradingArray.getJSONObject(i);
                Integer answerId = gradingObj.getInteger("answerId");
                BigDecimal score = gradingObj.getBigDecimal("studentScore");
                
                System.out.println("批改答案ID: " + answerId + ", 得分: " + score);
                
                // 根据answerId更新主观题得分
                StudentAnswer answer = getById(answerId);
                
                if (answer != null) {
                    answer.setStudentScore(score);
                    answer.setIsCorrect(score.compareTo(BigDecimal.ZERO) > 0 ? 1 : 0);
                    updateById(answer);
                    System.out.println("更新答案成功: " + answerId);
                } else {
                    System.out.println("未找到答案记录: " + answerId);
                }
            }
            
            // 重新计算总分并更新考试记录状态
            updateExamRecordScore(recordId);
            
            System.out.println("=== 批改完成 ===");
            return true;
        } catch (Exception e) {
            System.out.println("=== 批改失败 ===");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 检查客观题答案是否正确
     */
    private Integer checkObjectiveAnswer(Question question, String studentAnswer) {
        try {
            String correctAnswer = question.getCorrectAnswer();
            if (correctAnswer == null || correctAnswer.isEmpty()) {
                return 0;
            }
            
            // 去掉JSON格式的引号和方括号
            correctAnswer = correctAnswer.replaceAll("[\"\\[\\]]", "");
            studentAnswer = studentAnswer.replaceAll("[\"\\[\\]]", "");
            
            return correctAnswer.equals(studentAnswer) ? 1 : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 更新考试记录的得分
     */
    private void updateExamRecordScore(Integer recordId) {
        try {
            // 计算总得分
            QueryWrapper<StudentAnswer> wrapper = new QueryWrapper<>();
            wrapper.eq("record_id", recordId);
            List<StudentAnswer> answers = list(wrapper);
            
            BigDecimal totalScore = BigDecimal.ZERO;
            boolean hasUngraded = false;
            
            for (StudentAnswer answer : answers) {
                if (answer.getStudentScore() != null) {
                    totalScore = totalScore.add(answer.getStudentScore());
                } else {
                    // 有未批改的主观题
                    hasUngraded = true;
                }
            }
            
            // 更新考试记录
            StudentExamRecord record = studentExamRecordMapper.selectById(recordId);
            if (record != null) {
                record.setStudentScore(totalScore);
                if (!hasUngraded) {
                    // 所有题目都已批改，更新状态
                    record.setExamStatus(2); // 已批改
                } else {
                    // 还有主观题未批改
                    record.setExamStatus(1); // 待批改
                }
                studentExamRecordMapper.updateById(record);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}