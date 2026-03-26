package com.example.zhjypt.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.zhjypt.pojo.QuestionOption;

import java.util.List;

/**
 * <p>
 * 选择题选项表 服务类
 * </p>
 *
 * @author system
 * @since 2025-12-29
 */
public interface QuestionOptionService extends IService<QuestionOption> {

    /**
     * 根据题目ID获取选项列表
     */
    List<QuestionOption> getOptionsByQuestionId(Integer questionId);

    /**
     * 批量保存题目选项
     */
    boolean saveQuestionOptions(Integer questionId, List<QuestionOption> options);

    /**
     * 删除题目的所有选项
     */
    boolean deleteOptionsByQuestionId(Integer questionId);
}