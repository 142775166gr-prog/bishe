package com.example.zhjypt.controller;

import com.example.zhjypt.pojo.Question;
import com.example.zhjypt.service.QuestionService;
import com.example.zhjypt.service.QuestionOptionService;
import com.example.zhjypt.vo.ResultVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 题目表 前端控制器
 * </p>
 *
 * @author system
 * @since 2025-12-29
 */
@RestController
@RequestMapping("/question")
@Api(tags = "题目管理")
public class QuestionController {

    @Autowired
    private QuestionService questionService;
    
    @Autowired
    private QuestionOptionService questionOptionService;

    @GetMapping("/GetQuestionsByCourseId")
    @ApiOperation(value = "根据课程ID获取题目列表", notes = "根据课程ID获取题目列表")
    public ResultVO<List<Question>> GetQuestionsByCourseId(@RequestParam Integer courseId) {
        try {
            List<Question> questions = questionService.getQuestionsByCourseId(courseId);
            return ResultVO.success("获取成功", questions);
        } catch (Exception e) {
            return ResultVO.fail("获取题目列表失败：" + e.getMessage());
        }
    }

    @GetMapping("/AddQuestion")
    @ApiOperation(value = "创建题目", notes = "创建题目")
    public ResultVO<Question> AddQuestion(@RequestParam Integer courseId,
                                         @RequestParam Integer questionType,
                                         @RequestParam String questionContent,
                                         @RequestParam(required = false) String questionImage,
                                         @RequestParam(required = false) String correctAnswer,
                                         @RequestParam(required = false) String questionAnalysis,
                                         @RequestParam Integer difficultyLevel,
                                         @RequestParam Integer questionScore,
                                         @RequestParam(required = false) String optionsJson) {
        try {
            Question question = new Question();
            question.setCourseId(courseId);
            question.setQuestionType(questionType);
            question.setQuestionContent(questionContent);
            question.setQuestionImage(questionImage);
            question.setCorrectAnswer(correctAnswer);
            question.setQuestionAnalysis(questionAnalysis);
            question.setDifficultyLevel(difficultyLevel);
            question.setQuestionScore(questionScore);
            
            boolean success = questionService.createQuestionWithOptions(question, optionsJson);
            if (success) {
                return ResultVO.success("创建成功", question);
            } else {
                return ResultVO.fail("创建失败");
            }
        } catch (Exception e) {
            return ResultVO.fail("创建失败：" + e.getMessage());
        }
    }

    @GetMapping("/UpdateQuestion")
    @ApiOperation(value = "更新题目", notes = "更新题目")
    public ResultVO<Question> UpdateQuestion(@RequestParam Integer questionId,
                                           @RequestParam Integer courseId,
                                           @RequestParam Integer questionType,
                                           @RequestParam String questionContent,
                                           @RequestParam(required = false) String questionImage,
                                           @RequestParam(required = false) String correctAnswer,
                                           @RequestParam(required = false) String questionAnalysis,
                                           @RequestParam Integer difficultyLevel,
                                           @RequestParam Integer questionScore,
                                           @RequestParam(required = false) String optionsJson) {
        try {
            Question question = new Question();
            question.setQuestionId(questionId);
            question.setCourseId(courseId);
            question.setQuestionType(questionType);
            question.setQuestionContent(questionContent);
            question.setQuestionImage(questionImage);
            question.setCorrectAnswer(correctAnswer);
            question.setQuestionAnalysis(questionAnalysis);
            question.setDifficultyLevel(difficultyLevel);
            question.setQuestionScore(questionScore);
            
            boolean success = questionService.updateQuestionWithOptions(question, optionsJson);
            if (success) {
                return ResultVO.success("更新成功", question);
            } else {
                return ResultVO.fail("更新失败");
            }
        } catch (Exception e) {
            return ResultVO.fail("更新失败：" + e.getMessage());
        }
    }

    @GetMapping("/DeleteQuestion")
    @ApiOperation(value = "删除题目", notes = "删除题目")
    public ResultVO<Question> DeleteQuestion(@RequestParam Integer questionId) {
        try {
            boolean success = questionService.deleteQuestion(questionId);
            if (success) {
                return ResultVO.success("删除成功");
            } else {
                return ResultVO.fail("删除失败");
            }
        } catch (Exception e) {
            return ResultVO.fail("删除失败：" + e.getMessage());
        }
    }

    @GetMapping("/GetQuestionsByExamId")
    @ApiOperation(value = "根据考试ID获取题目列表", notes = "根据考试ID获取题目列表")
    public ResultVO<List<Question>> GetQuestionsByExamId(@RequestParam Integer examId) {
        try {
            List<Question> questions = questionService.getQuestionsByExamId(examId);
            return ResultVO.success("获取成功", questions);
        } catch (Exception e) {
            return ResultVO.fail("获取题目列表失败：" + e.getMessage());
        }
    }

    @GetMapping("/GetQuestion")
    @ApiOperation(value = "获取题目详情（包含选项）", notes = "获取题目详情（包含选项）")
    public ResultVO<Question> GetQuestion(@RequestParam Integer questionId) {
        try {
            Question question = questionService.getQuestionWithOptions(questionId);
            if (question != null) {
                return ResultVO.success("获取成功", question);
            } else {
                return ResultVO.fail("题目不存在");
            }
        } catch (Exception e) {
            return ResultVO.fail("获取题目详情失败：" + e.getMessage());
        }
    }
}