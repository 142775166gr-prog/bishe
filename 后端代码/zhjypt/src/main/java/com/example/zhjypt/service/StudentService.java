package com.example.zhjypt.service;

import com.example.zhjypt.pojo.Student;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.zhjypt.vo.ResultVO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author daswswsdgkj
 * @since 2025-12-22
 */
public interface StudentService extends IService<Student> {
    public ResultVO<Student> AddStudent(Student student);
    public ResultVO<Student> UpdateStudent(Student student);
    public ResultVO<Student> DeleteStudent(Student student);
    public ResultVO<Student> GetStudent(Student student);
    public ResultVO<List> GetStudentList(Student student);
    public ResultVO<List> GetStudentListByPage(Student student, int pageNum, int pageSize);
    // 学生登录
    public ResultVO<Student> StudentLogin(Student student);
    // 重置学生密码
    public ResultVO<Student> ResetStudentPassword(Integer stuId, String newPassword);
}
