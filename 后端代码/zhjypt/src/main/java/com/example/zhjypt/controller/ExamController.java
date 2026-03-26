package com.example.zhjypt.controller;

import com.example.zhjypt.pojo.Exam;
import com.example.zhjypt.pojo.StudentExamRecord;
import com.example.zhjypt.service.ExamService;
import com.example.zhjypt.vo.ResultVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 考试表 前端控制器
 * </p>
 *
 * @author system
 * @since 2025-12-29
 */
@RestController
@RequestMapping("/exam")
@Api(tags = "考试管理")
@CrossOrigin
public class ExamController {

    @Autowired
    private ExamService examService;

    @GetMapping("/GetExamsByCourseId")
    @ApiOperation(value = "根据课程ID获取考试列表", notes = "根据课程ID获取考试列表")
    public ResultVO<List<Exam>> GetExamsByCourseId(@RequestParam Integer courseId) {
        try {
            List<Exam> exams = examService.getExamsByCourseId(courseId);
            return ResultVO.success("获取成功", exams);
        } catch (Exception e) {
            return ResultVO.fail("获取考试列表失败：" + e.getMessage());
        }
    }

    @GetMapping("/AddExam")
    @ApiOperation(value = "创建考试", notes = "创建考试")
    public ResultVO<Exam> AddExam(Exam exam) {
        try {
            boolean success = examService.createExam(exam);
            if (success) {
                return ResultVO.success("创建成功", exam);
            } else {
                return ResultVO.fail("创建失败");
            }
        } catch (Exception e) {
            return ResultVO.fail("创建失败：" + e.getMessage());
        }
    }

    @GetMapping("/UpdateExam")
    @ApiOperation(value = "更新考试", notes = "更新考试")
    public ResultVO<Exam> UpdateExam(Exam exam) {
        try {
            boolean success = examService.updateExam(exam);
            if (success) {
                return ResultVO.success("更新成功", exam);
            } else {
                return ResultVO.fail("更新失败");
            }
        } catch (Exception e) {
            return ResultVO.fail("更新失败：" + e.getMessage());
        }
    }

    @GetMapping("/DeleteExam")
    @ApiOperation(value = "删除考试", notes = "删除考试")
    public ResultVO<Exam> DeleteExam(Exam exam) {
        try {
            boolean success = examService.deleteExam(exam.getExamId());
            if (success) {
                return ResultVO.success("删除成功");
            } else {
                return ResultVO.fail("删除失败");
            }
        } catch (Exception e) {
            return ResultVO.fail("删除失败：" + e.getMessage());
        }
    }

    @GetMapping("/PublishExam")
    @ApiOperation(value = "发布考试", notes = "发布考试")
    public ResultVO<Exam> PublishExam(Integer examId) {
        try {
            boolean success = examService.publishExam(examId);
            if (success) {
                return ResultVO.success("发布成功");
            } else {
                return ResultVO.fail("发布失败");
            }
        } catch (Exception e) {
            return ResultVO.fail("发布失败：" + e.getMessage());
        }
    }

    @GetMapping("/UnpublishExam")
    @ApiOperation(value = "取消发布考试", notes = "取消发布考试")
    public ResultVO<Exam> UnpublishExam(Integer examId) {
        try {
            boolean success = examService.unpublishExam(examId);
            if (success) {
                return ResultVO.success("取消发布成功");
            } else {
                return ResultVO.fail("取消发布失败");
            }
        } catch (Exception e) {
            return ResultVO.fail("取消发布失败：" + e.getMessage());
        }
    }

    @GetMapping("/GetAvailableExams")
    @ApiOperation(value = "获取学生可参加的考试列表", notes = "获取学生可参加的考试列表")
    public ResultVO<List<Exam>> GetAvailableExams(@RequestParam Integer courseId, @RequestParam Integer studentId) {
        try {
            List<Exam> exams = examService.getAvailableExams(courseId, studentId);
            return ResultVO.success("获取成功", exams);
        } catch (Exception e) {
            return ResultVO.fail("获取考试列表失败：" + e.getMessage());
        }
    }

    @GetMapping("/GetExam")
    @ApiOperation(value = "获取考试详情", notes = "获取考试详情")
    public ResultVO<Exam> GetExam(@RequestParam Integer examId) {
        try {
            Exam exam = examService.getById(examId);
            if (exam != null) {
                return ResultVO.success("获取成功", exam);
            } else {
                return ResultVO.fail("考试不存在");
            }
        } catch (Exception e) {
            return ResultVO.fail("获取考试详情失败：" + e.getMessage());
        }
    }

    @GetMapping("/GetStudentExamRecords")
    @ApiOperation(value = "获取学生某个考试的所有记录", notes = "获取学生某个考试的所有记录")
    public ResultVO<List<StudentExamRecord>> GetStudentExamRecords(@RequestParam Integer studentId, @RequestParam Integer examId) {
        try {
            List<StudentExamRecord> records = examService.getStudentExamRecords(studentId, examId);
            return ResultVO.success("获取成功", records);
        } catch (Exception e) {
            return ResultVO.fail("获取考试记录失败：" + e.getMessage());
        }
    }

    @GetMapping("/GetLatestExamRecord")
    @ApiOperation(value = "获取学生最新的考试记录", notes = "获取学生最新的考试记录")
    public ResultVO<StudentExamRecord> GetLatestExamRecord(@RequestParam Integer studentId, @RequestParam Integer examId) {
        try {
            StudentExamRecord record = examService.getLatestExamRecord(studentId, examId);
            if (record != null) {
                return ResultVO.success("获取成功", record);
            } else {
                return ResultVO.fail("未找到考试记录");
            }
        } catch (Exception e) {
            return ResultVO.fail("获取考试记录失败：" + e.getMessage());
        }
    }

    @GetMapping("/GetExamAttemptCount")
    @ApiOperation(value = "获取学生考试次数", notes = "获取学生考试次数")
    public ResultVO<Integer> GetExamAttemptCount(@RequestParam Integer studentId, @RequestParam Integer examId) {
        try {
            Integer count = examService.getExamAttemptCount(studentId, examId);
            return ResultVO.success("获取成功", count);
        } catch (Exception e) {
            return ResultVO.fail("获取考试次数失败：" + e.getMessage());
        }
    }

    @GetMapping("/CheckExamSubmitted")
    @ApiOperation(value = "检查考试是否已提交", notes = "检查学生是否已提交过该考试（不包括进行中的记录）")
    public ResultVO<Boolean> CheckExamSubmitted(@RequestParam Integer studentId, @RequestParam Integer examId) {
        try {
            boolean submitted = examService.hasSubmittedExam(studentId, examId);
            return ResultVO.success("检查成功", submitted);
        } catch (Exception e) {
            return ResultVO.fail("检查失败：" + e.getMessage());
        }
    }

    @GetMapping("/CreateExamRecord")
    @ApiOperation(value = "创建考试记录", notes = "创建考试记录")
    public ResultVO<StudentExamRecord> CreateExamRecord(@RequestParam Integer studentId,
                                                       @RequestParam Integer examId,
                                                       @RequestParam Integer totalScore) {
        try {
            StudentExamRecord record = examService.createExamRecord(studentId, examId, totalScore);
            if (record != null) {
                return ResultVO.success("创建成功", record);
            } else {
                return ResultVO.fail("创建失败");
            }
        } catch (Exception e) {
            return ResultVO.fail("创建考试记录失败：" + e.getMessage());
        }
    }

    @GetMapping("/SubmitExamRecord")
    @ApiOperation(value = "提交考试", notes = "提交考试")
    public ResultVO<String> SubmitExamRecord(@RequestParam Integer recordId,
                                            @RequestParam Integer timeUsed,
                                            @RequestParam(required = false) Double studentScore) {
        try {
            boolean success = examService.submitExamRecord(recordId, timeUsed, studentScore);
            if (success) {
                return ResultVO.success("提交成功");
            } else {
                return ResultVO.fail("提交失败");
            }
        } catch (Exception e) {
            return ResultVO.fail("提交考试失败：" + e.getMessage());
        }
    }

    @GetMapping("/GetRecordsForGrading")
    @ApiOperation(value = "获取需要批改的考试记录", notes = "获取需要批改的考试记录")
    public ResultVO<List<Map<String, Object>>> GetRecordsForGrading(@RequestParam Integer courseId) {
        try {
            List<Map<String, Object>> records = examService.getRecordsForGrading(courseId);
            return ResultVO.success("获取成功", records);
        } catch (Exception e) {
            return ResultVO.fail("获取批改记录失败：" + e.getMessage());
        }
    }

    @GetMapping("/SaveStudentAnswers")
    @ApiOperation(value = "保存学生答案", notes = "保存学生答案")
    public ResultVO<String> SaveStudentAnswers(@RequestParam Integer recordId,
                                              @RequestParam String answers) {
        try {
            // 暂时只打印日志，测试API是否可达
            System.out.println("收到保存学生答案请求:");
            System.out.println("recordId: " + recordId);
            System.out.println("answers: " + answers);
            
            // TODO: 实际保存到数据库的逻辑
            
            return ResultVO.success("答案保存成功");
        } catch (Exception e) {
            return ResultVO.fail("保存答案失败：" + e.getMessage());
        }
    }

    @GetMapping("/GradeExamRecord")
    @ApiOperation(value = "批改考试记录", notes = "批改考试记录")
    public ResultVO<String> GradeExamRecord(@RequestParam Integer recordId,
                                           @RequestParam(required = false) Double subjectiveScore,
                                           @RequestParam(required = false) String feedback) {
        try {
            boolean success = examService.gradeExamRecord(recordId, subjectiveScore, feedback);
            if (success) {
                return ResultVO.success("批改成功");
            } else {
                return ResultVO.fail("批改失败");
            }
        } catch (Exception e) {
            return ResultVO.fail("批改失败：" + e.getMessage());
        }
    }

    @GetMapping("/GetPendingGradingCount")
    @ApiOperation(value = "获取教师待批改数量", notes = "获取教师待批改数量")
    public ResultVO<Integer> GetPendingGradingCount(@RequestParam Integer teacherId) {
        try {
            Integer count = examService.getPendingGradingCount(teacherId);
            return ResultVO.success("获取成功", count);
        } catch (Exception e) {
            return ResultVO.fail("获取待批改数量失败：" + e.getMessage());
        }
    }

    @GetMapping("/GetPendingExamCount")
    @ApiOperation(value = "获取学生待完成考试数量", notes = "获取学生待完成考试数量")
    public ResultVO<Integer> GetPendingExamCount(@RequestParam Integer studentId) {
        try {
            Integer count = examService.getPendingExamCount(studentId);
            return ResultVO.success("获取成功", count);
        } catch (Exception e) {
            return ResultVO.fail("获取待完成考试数量失败：" + e.getMessage());
        }
    }
}
