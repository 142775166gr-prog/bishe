package com.example.zhjypt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.zhjypt.mapper.ConsultationMapper;
import com.example.zhjypt.pojo.Consultation;
import com.example.zhjypt.service.ConsultationService;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * <p>
 * 在线咨询服务实现类
 * </p>
 *
 * @author system
 * @since 2026-01-12
 */
@Service
public class ConsultationServiceImpl extends ServiceImpl<ConsultationMapper, Consultation> implements ConsultationService {

    @Override
    public boolean createConsultation(Consultation consultation) {
        consultation.setStatus(0); // 待回复
        consultation.setIsRead(0); // 未读
        consultation.setCreateTime(new Date());
        return this.save(consultation);
    }

    @Override
    public boolean replyConsultation(Integer consultationId, String replyContent, Integer teacherId) {
        Consultation consultation = this.getById(consultationId);
        if (consultation == null || !consultation.getTeacherId().equals(teacherId)) {
            return false;
        }
        
        consultation.setReplyContent(replyContent);
        consultation.setReplyTime(new Date());
        consultation.setStatus(1); // 已回复
        consultation.setIsRead(0); // 学生未读
        
        return this.updateById(consultation);
    }

    @Override
    public boolean closeConsultation(Integer consultationId) {
        Consultation consultation = this.getById(consultationId);
        if (consultation == null) {
            return false;
        }
        
        consultation.setStatus(2); // 已关闭
        return this.updateById(consultation);
    }

    @Override
    public boolean markAsRead(Integer consultationId, Integer studentId) {
        Consultation consultation = this.getById(consultationId);
        if (consultation == null || !consultation.getStudentId().equals(studentId)) {
            return false;
        }
        
        consultation.setIsRead(1);
        return this.updateById(consultation);
    }

    @Override
    public boolean setPriority(Integer consultationId, Integer priority, Integer teacherId) {
        Consultation consultation = this.getById(consultationId);
        if (consultation == null || !consultation.getTeacherId().equals(teacherId)) {
            return false;
        }
        
        consultation.setPriority(priority);
        return this.updateById(consultation);
    }

    @Override
    public Page<Consultation> getStudentConsultations(Integer studentId, Integer status, Integer pageNum, Integer pageSize) {
        Page<Consultation> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Consultation> wrapper = new QueryWrapper<>();
        wrapper.eq("student_id", studentId);
        
        if (status != null) {
            wrapper.eq("status", status);
        }
        
        wrapper.orderByDesc("create_time");
        
        return this.page(page, wrapper);
    }

    @Override
    public Page<Consultation> getTeacherConsultations(Integer teacherId, Integer status, Integer courseId, Integer pageNum, Integer pageSize) {
        Page<Consultation> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Consultation> wrapper = new QueryWrapper<>();
        wrapper.eq("teacher_id", teacherId);
        
        if (status != null) {
            wrapper.eq("status", status);
        }
        
        if (courseId != null) {
            wrapper.eq("course_id", courseId);
        }
        
        // 优先级高的排在前面，然后按创建时间倒序
        wrapper.orderByDesc("priority").orderByDesc("create_time");
        
        return this.page(page, wrapper);
    }

    @Override
    public Consultation getConsultationDetail(Integer consultationId) {
        return this.getById(consultationId);
    }

    @Override
    public boolean deleteConsultation(Integer consultationId, Integer userId, String userType) {
        Consultation consultation = this.getById(consultationId);
        if (consultation == null) {
            return false;
        }
        
        // 验证权限：学生只能删除自己的咨询，教师只能删除自己收到的咨询
        if ("student".equals(userType) && !consultation.getStudentId().equals(userId)) {
            return false;
        }
        if ("teacher".equals(userType) && !consultation.getTeacherId().equals(userId)) {
            return false;
        }
        
        return this.removeById(consultationId);
    }

    @Override
    public Integer getUnrepliedCountByTeacher(Integer teacherId) {
        QueryWrapper<Consultation> wrapper = new QueryWrapper<>();
        wrapper.eq("teacher_id", teacherId);
        wrapper.eq("status", 0);
        wrapper.eq("del", 0);
        long count = this.count(wrapper);
        return (int) count;
    }

    @Override
    public Integer getUnreadReplyCountByStudent(Integer studentId) {
        QueryWrapper<Consultation> wrapper = new QueryWrapper<>();
        wrapper.eq("student_id", studentId);
        wrapper.eq("status", 1);
        wrapper.eq("is_read", 0);
        wrapper.eq("del", 0);
        long count = this.count(wrapper);
        return (int) count;
    }
}
