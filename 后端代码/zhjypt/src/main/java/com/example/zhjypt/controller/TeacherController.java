package com.example.zhjypt.controller;


import com.example.zhjypt.pojo.Teacher;
import com.example.zhjypt.service.TeacherService;
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
@RequestMapping("/Teacher")
@CrossOrigin
public class TeacherController {
    @Autowired
    private TeacherService teacherService;

    @GetMapping("/AddTeacher")
    @ApiOperation(value = "添加教师",notes = "添加教师")
    public ResultVO<Teacher> AddTeacher(Teacher teacher){
        return teacherService.AddTeacher(teacher);
    }
    @GetMapping("/UpdateTeacher")
    @ApiOperation(value = "修改教师",notes = "修改教师")
    public ResultVO<Teacher> UpdateTeacher(Teacher teacher){
        return teacherService.UpdateTeacher(teacher);
    }
    @GetMapping("/DeleteTeacher")
    @ApiOperation(value = "删除教师",notes = "删除教师")
    public ResultVO<Teacher> DeleteTeacher(Teacher teacher){
        return teacherService.DeleteTeacher(teacher);
    }
    @GetMapping("/GetTeacher")
    @ApiOperation(value = "获取教师",notes = "获取教师")
    public ResultVO<Teacher> GetTeacher(Teacher teacher){
        return teacherService.GetTeacher(teacher);
    }
    @GetMapping("/GetTeacherList")
    @ApiOperation(value = "获取教师列表",notes = "获取教师列表")
    public ResultVO<List> GetTeacherList(Teacher teacher){
        return teacherService.GetTeacherList(teacher);
    }
    @GetMapping("/TeacherLogin")
    @ApiOperation(value = "老师登录",notes = "老师登录")
    public ResultVO<Teacher> TeacherLogin(Teacher teacher) {
        return teacherService.TeacherLogin(teacher);
    }
    @GetMapping("/GetTeacherListByPage")
    @ApiOperation(value = "分页获取教师列表",notes = "分页获取教师列表")
    public ResultVO<List> GetTeacherListByPage(Teacher teacher,int pageNum,int pageSize, javax.servlet.http.HttpServletRequest request){
        System.out.println("[DEBUG] GetTeacherListByPage called with teachAccount=" + teacher.getTeachAccount() + ", teachName=" + teacher.getTeachName() + ", pageNum=" + pageNum + ", pageSize=" + pageSize);
        System.out.println("[DEBUG] raw queryString=" + request.getQueryString());
        java.util.Map<String,String[]> map = request.getParameterMap();
        System.out.println("[DEBUG] paramMap=" + map);
        return teacherService.GetTeacherListByPage(teacher,pageNum,pageSize);
    }

    @GetMapping("/ResetTeacherPassword")
    @ApiOperation(value = "重置教师密码",notes = "将教师密码重置为默认值")
    public ResultVO<Teacher> ResetTeacherPassword(Integer teachId){
        // 默认密码 123456
        return teacherService.ResetTeacherPassword(teachId,"123456");
    }
}
