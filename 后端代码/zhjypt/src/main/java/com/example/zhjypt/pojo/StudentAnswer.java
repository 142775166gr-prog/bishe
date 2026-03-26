package com.example.zhjypt.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 学生答题记录表
 * </p>
 *
 * @author system
 * @since 2026-01-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("student_answer")
public class StudentAnswer implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 答题记录ID
     */
    @TableId(value = "answer_id", type = IdType.AUTO)
    private Integer answerId;

    /**
     * 考试记录ID
     */
    private Integer recordId;

    /**
     * 题目ID
     */
    private Integer questionId;

    /**
     * 学生答案（JSON格式）
     */
    private String studentAnswer;

    /**
     * 是否正确：0错误，1正确，NULL未评分
     */
    private Integer isCorrect;

    /**
     * 题目分值
     */
    private Integer questionScore;

    /**
     * 学生得分
     */
    private BigDecimal studentScore;

    /**
     * 答题时间
     */
    private Date answerTime;
}