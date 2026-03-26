package com.example.zhjypt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.zhjypt.pojo.StudentExamRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 学生考试记录表 Mapper 接口
 * </p>
 *
 * @author system
 * @since 2025-12-29
 */
@Mapper
public interface StudentExamRecordMapper extends BaseMapper<StudentExamRecord> {

    /**
     * 获取学生考试记录
     */
    List<StudentExamRecord> selectRecordsByStudentAndExam(@Param("studentId") Integer studentId, @Param("examId") Integer examId);

    /**
     * 获取学生最新考试记录
     */
    StudentExamRecord selectLatestRecord(@Param("studentId") Integer studentId, @Param("examId") Integer examId);

    /**
     * 获取学生考试次数
     */
    Integer countAttempts(@Param("studentId") Integer studentId, @Param("examId") Integer examId);

    /**
     * 获取需要批改的考试记录
     */
    List<Map<String, Object>> selectRecordsForGrading(@Param("courseId") Integer courseId);
}