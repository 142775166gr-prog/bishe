package com.example.zhjypt.controller;

import com.example.zhjypt.pojo.StudentAnswer;
import com.example.zhjypt.service.StudentAnswerService;
import com.example.zhjypt.vo.ResultVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 学生答题记录表 前端控制器
 * </p>
 *
 * @author system
 * @since 2026-01-09
 */
@RestController
@RequestMapping("/studentAnswer")
@Api(tags = "学生答题管理")
@CrossOrigin
public class StudentAnswerController {

    @Autowired
    private StudentAnswerService studentAnswerService;

    @PostMapping("/BatchSaveAnswers")
    @ApiOperation(value = "批量保存学生答案", notes = "批量保存学生答案")
    public ResultVO<String> BatchSaveAnswers(@RequestParam Integer recordId,
                                           @RequestParam String answers) {
        try {
            System.out.println("收到批量保存请求 - recordId: " + recordId);
            System.out.println("答案数据: " + answers);
            
            boolean success = studentAnswerService.batchSaveStudentAnswers(recordId, answers);
            if (success) {
                return ResultVO.success("批量保存成功");
            } else {
                return ResultVO.fail("批量保存失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultVO.fail("批量保存失败：" + e.getMessage());
        }
    }

    @GetMapping("/test")
    @ApiOperation(value = "测试接口", notes = "测试接口")
    public ResultVO<String> test() {
        return ResultVO.success("StudentAnswer API 正常工作");
    }

    @GetMapping("/GetAnswersByRecord")
    @ApiOperation(value = "获取考试记录的所有答案", notes = "获取考试记录的所有答案")
    public ResultVO<List<Map<String, Object>>> GetAnswersByRecord(@RequestParam Integer recordId) {
        try {
            System.out.println("=== 开始获取答案，recordId: " + recordId);
            List<Map<String, Object>> answers = studentAnswerService.getStudentAnswersWithQuestions(recordId);
            System.out.println("=== 查询到的答案数量: " + (answers != null ? answers.size() : 0));
            if (answers != null && answers.size() > 0) {
                System.out.println("=== 第一条答案: " + answers.get(0));
            }
            return ResultVO.success("获取成功", answers);
        } catch (Exception e) {
            System.out.println("=== 获取答案失败: " + e.getMessage());
            e.printStackTrace();
            return ResultVO.fail("获取失败：" + e.getMessage());
        }
    }

    @PostMapping("/GradeSubjectiveAnswers")
    @ApiOperation(value = "批改主观题", notes = "批改主观题")
    public ResultVO<String> GradeSubjectiveAnswers(@RequestParam Integer recordId,
                                                  @RequestParam String gradingData) {
        try {
            boolean success = studentAnswerService.gradeSubjectiveAnswers(recordId, gradingData);
            if (success) {
                return ResultVO.success("批改成功");
            } else {
                return ResultVO.fail("批改失败");
            }
        } catch (Exception e) {
            return ResultVO.fail("批改失败：" + e.getMessage());
        }
    }
}