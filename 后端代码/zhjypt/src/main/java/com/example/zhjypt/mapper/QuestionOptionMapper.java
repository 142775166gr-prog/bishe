package com.example.zhjypt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.zhjypt.pojo.QuestionOption;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 选择题选项表 Mapper 接口
 * </p>
 *
 * @author system
 * @since 2025-12-29
 */
@Mapper
public interface QuestionOptionMapper extends BaseMapper<QuestionOption> {

    /**
     * 根据题目ID获取选项列表
     */
    List<QuestionOption> selectOptionsByQuestionId(@Param("questionId") Integer questionId);

    /**
     * 批量插入选项
     */
    int insertBatch(@Param("options") List<QuestionOption> options);
}