package com.example.zhjypt.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.zhjypt.pojo.Consultation;

/**
 * <p>
 * 在线咨询服务类
 * </p>
 *
 * @author system
 * @since 2026-01-12
 */
public interface ConsultationService extends IService<Consultation> {

    /**
     * 学生创建咨询
     */
    boolean createConsultation(Consultation consultation);

    /**
     * 教师回复咨询
     */
    boolean replyConsultation(Integer consultationId, String replyContent, Integer teacherId);

    /**
     * 关闭咨询
     */
    boolean closeConsultation(Integer consultationId);

    /**
     * 标记为已读
     */
    boolean markAsRead(Integer consultationId, Integer studentId);

    /**
     * 设置优先级
     */
    boolean setPriority(Integer consultationId, Integer priority, Integer teacherId);

    /**
     * 获取学生咨询列表（分页）
     */
    Page<Consultation> getStudentConsultations(Integer studentId, Integer status, Integer pageNum, Integer pageSize);

    /**
     * 获取教师咨询列表（分页）
     */
    Page<Consultation> getTeacherConsultations(Integer teacherId, Integer status, Integer courseId, Integer pageNum, Integer pageSize);

    /**
     * 获取咨询详情
     */
    Consultation getConsultationDetail(Integer consultationId);

    /**
     * 删除咨询
     */
    boolean deleteConsultation(Integer consultationId, Integer userId, String userType);

    /**
     * 获取教师未回复咨询数量
     */
    Integer getUnrepliedCountByTeacher(Integer teacherId);

    /**
     * 获取学生未读回复数量
     */
    Integer getUnreadReplyCountByStudent(Integer studentId);
}
