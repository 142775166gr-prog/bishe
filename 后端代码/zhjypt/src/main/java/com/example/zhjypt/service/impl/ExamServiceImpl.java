package com.example.zhjypt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.zhjypt.mapper.CourseMapper;
import com.example.zhjypt.mapper.ExamMapper;
import com.example.zhjypt.mapper.StudentCourseMapper;
import com.example.zhjypt.mapper.StudentExamRecordMapper;
import com.example.zhjypt.pojo.Course;
import com.example.zhjypt.pojo.Exam;
import com.example.zhjypt.pojo.StudentCourse;
import com.example.zhjypt.pojo.StudentExamRecord;
import com.example.zhjypt.service.ExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 考试表 服务实现类
 * </p>
 *
 * @author system
 * @since 2025-12-29
 */
@Service
public class ExamServiceImpl extends ServiceImpl<ExamMapper, Exam> implements ExamService {

    @Autowired
    private ExamMapper examMapper;
    
    @Autowired
    private StudentExamRecordMapper studentExamRecordMapper;
    
    @Autowired
    private CourseMapper courseMapper;
    
    @Autowired
    private StudentCourseMapper studentCourseMapper;

    @Override
    public List<Exam> getExamsByCourseId(Integer courseId) {
        QueryWrapper<Exam> wrapper = new QueryWrapper<>();
        wrapper.eq("course_id", courseId)
               .eq("del", 0)
               .orderByDesc("create_time");
        return list(wrapper);
    }

    @Override
    public boolean createExam(Exam exam) {
        exam.setCreateTime(new Date());
        exam.setUpdateTime(new Date());
        exam.setExamStatus(0); // 默认草稿状态
        exam.setDel(0);
        return save(exam);
    }

    @Override
    public boolean updateExam(Exam exam) {
        exam.setUpdateTime(new Date());
        return updateById(exam);
    }

    @Override
    public boolean deleteExam(Integer examId) {
        return removeById(examId);
    }

    @Override
    public boolean publishExam(Integer examId) {
        Exam exam = new Exam();
        exam.setExamId(examId);
        exam.setExamStatus(1);
        exam.setUpdateTime(new Date());
        return updateById(exam);
    }

    @Override
    public boolean unpublishExam(Integer examId) {
        Exam exam = new Exam();
        exam.setExamId(examId);
        exam.setExamStatus(0);
        exam.setUpdateTime(new Date());
        return updateById(exam);
    }

    @Override
    public List<Exam> getAvailableExams(Integer courseId, Integer studentId) {
        QueryWrapper<Exam> wrapper = new QueryWrapper<>();
        wrapper.eq("course_id", courseId)
               .eq("exam_status", 1) // 已发布
               .eq("del", 0)
               .and(w -> w.isNull("start_time").or().le("start_time", new Date()))
               .and(w -> w.isNull("end_time").or().ge("end_time", new Date()))
               .orderByDesc("create_time");
        return list(wrapper);
    }

    @Override
    public List<StudentExamRecord> getStudentExamRecords(Integer studentId, Integer examId) {
        return studentExamRecordMapper.selectRecordsByStudentAndExam(studentId, examId);
    }

    @Override
    public StudentExamRecord getLatestExamRecord(Integer studentId, Integer examId) {
        return studentExamRecordMapper.selectLatestRecord(studentId, examId);
    }

    @Override
    public Integer getExamAttemptCount(Integer studentId, Integer examId) {
        Integer count = studentExamRecordMapper.countAttempts(studentId, examId);
        return count != null ? count : 0;
    }

    @Override
    public boolean hasSubmittedExam(Integer studentId, Integer examId) {
        try {
            // 查询是否有已提交（exam_status=1）或已批改（exam_status=2）的记录
            QueryWrapper<StudentExamRecord> wrapper = new QueryWrapper<>();
            wrapper.eq("student_id", studentId)
                   .eq("exam_id", examId)
                   .and(w -> w.eq("exam_status", 1).or().eq("exam_status", 2));
            
            return studentExamRecordMapper.selectCount(wrapper) > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public StudentExamRecord createExamRecord(Integer studentId, Integer examId, Integer totalScore) {
        try {
            // 获取当前考试次数
            Integer attemptCount = getExamAttemptCount(studentId, examId);
            
            StudentExamRecord record = new StudentExamRecord();
            record.setStudentId(studentId);
            record.setExamId(examId);
            record.setTotalScore(totalScore);
            record.setAttemptNumber(attemptCount + 1);
            record.setStartTime(new Date());
            record.setExamStatus(0); // 进行中
            record.setCreateTime(new Date());
            
            // 使用MyBatis Plus保存
            QueryWrapper<StudentExamRecord> wrapper = new QueryWrapper<>();
            wrapper.setEntity(record);
            
            // 直接插入数据库
            int result = studentExamRecordMapper.insert(record);
            
            return result > 0 ? record : null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean submitExamRecord(Integer recordId, Integer timeUsed, Double studentScore) {
        try {
            StudentExamRecord record = studentExamRecordMapper.selectById(recordId);
            if (record != null) {
                record.setSubmitTime(new Date());
                record.setTimeUsed(timeUsed);
                if (studentScore != null) {
                    record.setStudentScore(BigDecimal.valueOf(studentScore));
                    // 判断是否通过（需要获取考试的及格分数）
                    Exam exam = getById(record.getExamId());
                    if (exam != null) {
                        record.setPassStatus(studentScore >= exam.getPassScore() ? 1 : 0);
                    }
                    record.setExamStatus(2); // 已评分
                } else {
                    record.setExamStatus(1); // 已提交
                }
                
                int result = studentExamRecordMapper.updateById(record);
                return result > 0;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Map<String, Object>> getRecordsForGrading(Integer courseId) {
        return studentExamRecordMapper.selectRecordsForGrading(courseId);
    }

    @Override
    public boolean gradeExamRecord(Integer recordId, Double subjectiveScore, String feedback) {
        try {
            StudentExamRecord record = studentExamRecordMapper.selectById(recordId);
            if (record != null) {
                // 获取当前客观题得分
                Double objectiveScore = record.getStudentScore() != null ? record.getStudentScore().doubleValue() : 0.0;
                
                // 计算总分（客观题 + 主观题）
                Double totalScore = objectiveScore + (subjectiveScore != null ? subjectiveScore : 0.0);
                
                record.setStudentScore(BigDecimal.valueOf(totalScore));
                record.setExamStatus(2); // 已评分
                
                // 判断是否通过
                Exam exam = getById(record.getExamId());
                if (exam != null) {
                    record.setPassStatus(totalScore >= exam.getPassScore() ? 1 : 0);
                }
                
                int result = studentExamRecordMapper.updateById(record);
                return result > 0;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Integer getPendingGradingCount(Integer teacherId) {
        try {
            // 查询教师的所有课程
            QueryWrapper<Course> courseWrapper = new QueryWrapper<>();
            courseWrapper.eq("teacher_id", teacherId);
            courseWrapper.eq("del", 0);
            List<Course> courses = courseMapper.selectList(courseWrapper);
            
            if (courses == null || courses.isEmpty()) {
                return 0;
            }
            
            // 统计所有课程的待批改考试记录
            int totalCount = 0;
            for (Course course : courses) {
                // 获取该课程的所有考试
                QueryWrapper<Exam> examWrapper = new QueryWrapper<>();
                examWrapper.eq("course_id", course.getCourseId());
                examWrapper.eq("del", 0);
                List<Exam> exams = this.list(examWrapper);
                
                for (Exam exam : exams) {
                    // 统计待批改的考试记录（已提交但未评分）
                    QueryWrapper<StudentExamRecord> recordWrapper = new QueryWrapper<>();
                    recordWrapper.eq("exam_id", exam.getExamId());
                    recordWrapper.eq("exam_status", 1); // 已提交
                    totalCount += studentExamRecordMapper.selectCount(recordWrapper).intValue();
                }
            }
            
            return totalCount;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public Integer getPendingExamCount(Integer studentId) {
        try {
            // 查询学生选修的所有课程
            QueryWrapper<StudentCourse> scWrapper = new QueryWrapper<>();
            scWrapper.eq("student_id", studentId);
            List<StudentCourse> studentCourses = studentCourseMapper.selectList(scWrapper);
            
            if (studentCourses == null || studentCourses.isEmpty()) {
                return 0;
            }
            
            // 统计所有课程的待完成考试
            int totalCount = 0;
            for (StudentCourse sc : studentCourses) {
                // 获取该课程的所有已发布考试
                QueryWrapper<Exam> examWrapper = new QueryWrapper<>();
                examWrapper.eq("course_id", sc.getCourseId());
                examWrapper.eq("exam_status", 1); // 已发布
                examWrapper.eq("del", 0);
                List<Exam> exams = this.list(examWrapper);
                
                for (Exam exam : exams) {
                    // 检查学生是否已完成该考试
                    QueryWrapper<StudentExamRecord> recordWrapper = new QueryWrapper<>();
                    recordWrapper.eq("student_id", studentId);
                    recordWrapper.eq("exam_id", exam.getExamId());
                    recordWrapper.in("exam_status", 1, 2); // 已提交或已评分
                    
                    if (studentExamRecordMapper.selectCount(recordWrapper) == 0) {
                        totalCount++; // 未完成的考试
                    }
                }
            }
            
            return totalCount;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
