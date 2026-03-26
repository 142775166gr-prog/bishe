package com.example.zhjypt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.zhjypt.pojo.StudentAnswer;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 学生答题记录表 Mapper 接口
 * </p>
 *
 * @author system
 * @since 2026-01-09
 */
@Mapper
public interface StudentAnswerMapper extends BaseMapper<StudentAnswer> {

    /**
     * 根据考试记录ID获取学生答案（包含题目信息）
     */
    List<Map<String, Object>> selectAnswersByRecordId(Integer recordId);
}