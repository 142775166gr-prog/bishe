package com.example.zhjypt.controller;


import com.example.zhjypt.pojo.Student;
import com.example.zhjypt.service.StudentService;
import com.example.zhjypt.vo.ResultVO;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author daswswsdgkj
 * @since 2025-12-22
 */
@RestController
@RequestMapping("/Student")
@CrossOrigin
public class StudentController {
    @Autowired
    private StudentService studentService;

    @GetMapping("/AddStudent")
    @ApiOperation(value = "添加学生",notes = "添加学生")
    public ResultVO<Student> AddStudent(Student student){
        return studentService.AddStudent(student);
    }
    @GetMapping("/UpdateStudent")
    @ApiOperation(value = "修改学生",notes = "修改学生")
    public ResultVO<Student> UpdateStudent(Student student){
        return studentService.UpdateStudent(student);
    }
    @GetMapping("/DeleteStudent")
    @ApiOperation(value = "删除学生",notes = "删除学生")
    public ResultVO<Student> DeleteStudent(Student student){
        return studentService.DeleteStudent(student);
    }
    @GetMapping("/GetStudent")
    @ApiOperation(value = "获取学生",notes = "获取学生")
    public ResultVO<Student> GetStudent(Student student){
        return studentService.GetStudent(student);
    }
    @GetMapping("/GetStudentList")
    @ApiOperation(value = "获取学生列表",notes = "获取学生列表")
    public ResultVO<List> GetStudentList(Student student){
        return studentService.GetStudentList(student);
    }

    @GetMapping("/StudentLogin")
    @ApiOperation(value = "学生登录",notes = "学生登录")
    public ResultVO<Student> StudentLogin(Student student) {
        return studentService.StudentLogin(student);
    }

    @GetMapping("/GetStudentListByPage")
    @ApiOperation(value = "分页获取学生列表",notes = "分页获取学生列表")
    public ResultVO<List> GetStudentListByPage(Student student,int pageNum,int pageSize, javax.servlet.http.HttpServletRequest request){
        System.out.println("[DEBUG] GetStudentListByPage called with stuAccount=" + student.getStuAccount() + ", stuName=" + student.getStuName() + ", pageNum=" + pageNum + ", pageSize=" + pageSize);
        System.out.println("[DEBUG] raw queryString=" + request.getQueryString());
        java.util.Map<String,String[]> map = request.getParameterMap();
        System.out.println("[DEBUG] paramMap=" + map);
        return studentService.GetStudentListByPage(student,pageNum,pageSize);
    }

    @GetMapping("/ResetStudentPassword")
    @ApiOperation(value = "重置学生密码",notes = "将学生密码重置为默认值")
    public ResultVO<Student> ResetStudentPassword(Integer stuId){
        // 默认密码 123456
        return studentService.ResetStudentPassword(stuId,"123456");
    }
}
