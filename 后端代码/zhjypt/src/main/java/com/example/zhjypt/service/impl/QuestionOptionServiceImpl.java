package com.example.zhjypt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.zhjypt.mapper.QuestionOptionMapper;
import com.example.zhjypt.pojo.QuestionOption;
import com.example.zhjypt.service.QuestionOptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 选择题选项表 服务实现类
 * </p>
 *
 * @author system
 * @since 2025-12-29
 */
@Service
public class QuestionOptionServiceImpl extends ServiceImpl<QuestionOptionMapper, QuestionOption> implements QuestionOptionService {

    @Autowired
    private QuestionOptionMapper questionOptionMapper;

    @Override
    public List<QuestionOption> getOptionsByQuestionId(Integer questionId) {
        QueryWrapper<QuestionOption> wrapper = new QueryWrapper<>();
        wrapper.eq("question_id", questionId)
               .orderByAsc("sort_order");
        return list(wrapper);
    }

    @Override
    public boolean saveQuestionOptions(Integer questionId, List<QuestionOption> options) {
        try {
            // 先删除原有选项
            deleteOptionsByQuestionId(questionId);
            
            // 保存新选项
            if (options != null && !options.isEmpty()) {
                for (QuestionOption option : options) {
                    option.setQuestionId(questionId);
                    save(option);
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteOptionsByQuestionId(Integer questionId) {
        try {
            QueryWrapper<QuestionOption> wrapper = new QueryWrapper<>();
            wrapper.eq("question_id", questionId);
            remove(wrapper);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}