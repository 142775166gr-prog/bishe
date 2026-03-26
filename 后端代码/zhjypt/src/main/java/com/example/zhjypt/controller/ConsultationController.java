package com.example.zhjypt.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.zhjypt.pojo.Consultation;
import com.example.zhjypt.service.ConsultationService;
import com.example.zhjypt.vo.ResultVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 在线咨询 前端控制器
 * </p>
 *
 * @author system
 * @since 2026-01-12
 */
@RestController
@RequestMapping("/consultation")
@Api(tags = "在线咨询管理")
@CrossOrigin
public class ConsultationController {

    @Autowired
    private ConsultationService consultationService;

    @ApiOperation("学生创建咨询")
    @GetMapping("/create")
    public ResultVO createConsultation(
            @RequestParam Integer studentId,
            @RequestParam String studentName,
            @RequestParam Integer teacherId,
            @RequestParam String teacherName,
            @RequestParam(required = false) Integer courseId,
            @RequestParam(required = false) String courseTitle,
            @RequestParam String questionTitle,
            @RequestParam String questionContent,
            @RequestParam(required = false) String questionImage,
            @RequestParam(defaultValue = "0") Integer priority) {
        try {
            Consultation consultation = new Consultation();
            consultation.setStudentId(studentId);
            consultation.setStudentName(studentName);
            consultation.setTeacherId(teacherId);
            consultation.setTeacherName(teacherName);
            consultation.setCourseId(courseId);
            consultation.setCourseTitle(courseTitle);
            consultation.setQuestionTitle(questionTitle);
            consultation.setQuestionContent(questionContent);
            consultation.setQuestionImage(questionImage);
            consultation.setPriority(priority);
            
            boolean success = consultationService.createConsultation(consultation);
            if (success) {
                return ResultVO.success("咨询提交成功");
            } else {
                return ResultVO.fail("咨询提交失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultVO.fail("咨询提交失败");
        }
    }

    @ApiOperation("教师回复咨询")
    @GetMapping("/reply")
    public ResultVO replyConsultation(
            @RequestParam Integer consultationId,
            @RequestParam String replyContent,
            @RequestParam Integer teacherId) {
        try {
            boolean success = consultationService.replyConsultation(consultationId, replyContent, teacherId);
            if (success) {
                return ResultVO.success("回复成功");
            } else {
                return ResultVO.fail("回复失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultVO.fail("回复失败");
        }
    }

    @ApiOperation("关闭咨询")
    @GetMapping("/close")
    public ResultVO closeConsultation(@RequestParam Integer consultationId) {
        try {
            boolean success = consultationService.closeConsultation(consultationId);
            if (success) {
                return ResultVO.success("关闭成功");
            } else {
                return ResultVO.fail("关闭失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultVO.fail("关闭失败");
        }
    }

    @ApiOperation("标记为已读")
    @GetMapping("/markread")
    public ResultVO markAsRead(
            @RequestParam Integer consultationId,
            @RequestParam Integer studentId) {
        try {
            boolean success = consultationService.markAsRead(consultationId, studentId);
            if (success) {
                return ResultVO.success("标记成功");
            } else {
                return ResultVO.fail("标记失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultVO.fail("标记失败");
        }
    }

    @ApiOperation("设置优先级")
    @GetMapping("/setpriority")
    public ResultVO setPriority(
            @RequestParam Integer consultationId,
            @RequestParam Integer priority,
            @RequestParam Integer teacherId) {
        try {
            boolean success = consultationService.setPriority(consultationId, priority, teacherId);
            if (success) {
                return ResultVO.success("设置成功");
            } else {
                return ResultVO.fail("设置失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultVO.fail("设置失败");
        }
    }

    @ApiOperation("获取学生咨询列表")
    @GetMapping("/student/list")
    public ResultVO getStudentConsultations(
            @RequestParam Integer studentId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            Page<Consultation> page = consultationService.getStudentConsultations(studentId, status, pageNum, pageSize);
            return ResultVO.success("查询成功", page.getRecords(), page.getTotal());
        } catch (Exception e) {
            e.printStackTrace();
            return ResultVO.fail("查询失败");
        }
    }

    @ApiOperation("获取教师咨询列表")
    @GetMapping("/teacher/list")
    public ResultVO getTeacherConsultations(
            @RequestParam Integer teacherId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer courseId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            Page<Consultation> page = consultationService.getTeacherConsultations(teacherId, status, courseId, pageNum, pageSize);
            return ResultVO.success("查询成功", page.getRecords(), page.getTotal());
        } catch (Exception e) {
            e.printStackTrace();
            return ResultVO.fail("查询失败");
        }
    }

    @ApiOperation("获取咨询详情")
    @GetMapping("/detail")
    public ResultVO getConsultationDetail(@RequestParam Integer consultationId) {
        try {
            Consultation consultation = consultationService.getConsultationDetail(consultationId);
            if (consultation != null) {
                return ResultVO.success("查询成功", consultation);
            } else {
                return ResultVO.fail("咨询不存在");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultVO.fail("查询失败");
        }
    }

    @ApiOperation("删除咨询")
    @GetMapping("/delete")
    public ResultVO deleteConsultation(
            @RequestParam Integer consultationId,
            @RequestParam Integer userId,
            @RequestParam String userType) {
        try {
            boolean success = consultationService.deleteConsultation(consultationId, userId, userType);
            if (success) {
                return ResultVO.success("删除成功");
            } else {
                return ResultVO.fail("删除失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultVO.fail("删除失败");
        }
    }

    @ApiOperation("获取教师未回复咨询数量")
    @GetMapping("/teacher/unreplied/count")
    public ResultVO getUnrepliedCount(@RequestParam Integer teacherId) {
        try {
            Integer count = consultationService.getUnrepliedCountByTeacher(teacherId);
            return ResultVO.success("查询成功", count);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultVO.fail("查询失败");
        }
    }

    @ApiOperation("获取学生未读回复数量")
    @GetMapping("/student/unread/count")
    public ResultVO getUnreadReplyCount(@RequestParam Integer studentId) {
        try {
            Integer count = consultationService.getUnreadReplyCountByStudent(studentId);
            return ResultVO.success("查询成功", count);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultVO.fail("查询失败");
        }
    }
}
