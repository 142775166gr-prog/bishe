package com.example.zhjypt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.zhjypt.mapper.QuestionMapper;
import com.example.zhjypt.mapper.QuestionOptionMapper;
import com.example.zhjypt.pojo.Question;
import com.example.zhjypt.pojo.QuestionOption;
import com.example.zhjypt.service.QuestionService;
import com.example.zhjypt.service.QuestionOptionService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 题目表 服务实现类
 * </p>
 *
 * @author system
 * @since 2025-12-29
 */
@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question> implements QuestionService {

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private QuestionOptionMapper questionOptionMapper;
    
    @Autowired
    private QuestionOptionService questionOptionService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<Question> getQuestionsByCourseId(Integer courseId) {
        QueryWrapper<Question> wrapper = new QueryWrapper<>();
        wrapper.eq("course_id", courseId)
               .eq("del", 0)
               .orderByDesc("create_time");
        List<Question> questions = list(wrapper);
        
        // 为每个题目加载选项
        for (Question question : questions) {
            if (question.getQuestionType() == 1 || question.getQuestionType() == 2) {
                List<QuestionOption> options = questionOptionService.getOptionsByQuestionId(question.getQuestionId());
                question.setOptions(options);
            }
        }
        
        return questions;
    }

    @Override
    @Transactional
    public boolean createQuestionWithOptions(Question question, String optionsJson) {
        try {
            // 保存题目
            question.setCreateTime(new Date());
            question.setUpdateTime(new Date());
            question.setDel(0);
            boolean success = save(question);
            
            if (success && optionsJson != null && !optionsJson.trim().isEmpty()) {
                // 解析并保存选项
                List<QuestionOption> options = objectMapper.readValue(optionsJson, new TypeReference<List<QuestionOption>>() {});
                questionOptionService.saveQuestionOptions(question.getQuestionId(), options);
            }
            
            return success;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    @Transactional
    public boolean updateQuestionWithOptions(Question question, String optionsJson) {
        try {
            // 更新题目
            question.setUpdateTime(new Date());
            boolean success = updateById(question);
            
            if (success && optionsJson != null && !optionsJson.trim().isEmpty()) {
                // 解析并更新选项
                List<QuestionOption> options = objectMapper.readValue(optionsJson, new TypeReference<List<QuestionOption>>() {});
                questionOptionService.saveQuestionOptions(question.getQuestionId(), options);
            }
            
            return success;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean createQuestion(Question question) {
        question.setCreateTime(new Date());
        question.setUpdateTime(new Date());
        question.setDel(0);
        return save(question);
    }

    @Override
    public boolean updateQuestion(Question question) {
        question.setUpdateTime(new Date());
        return updateById(question);
    }

    @Override
    @Transactional
    public boolean deleteQuestion(Integer questionId) {
        try {
            // 删除题目选项
            questionOptionService.deleteOptionsByQuestionId(questionId);
            // 删除题目
            return removeById(questionId);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Question> getQuestionsByExamId(Integer examId) {
        return questionMapper.selectQuestionsByExamId(examId);
    }

    @Override
    public Question getQuestionWithOptions(Integer questionId) {
        Question question = getById(questionId);
        if (question != null && (question.getQuestionType() == 1 || question.getQuestionType() == 2)) {
            List<QuestionOption> options = questionOptionService.getOptionsByQuestionId(questionId);
            question.setOptions(options);
        }
        return question;
    }
}