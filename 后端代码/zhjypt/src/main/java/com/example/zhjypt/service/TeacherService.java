package com.example.zhjypt.service;

import com.example.zhjypt.pojo.Teacher;
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
public interface TeacherService extends IService<Teacher> {
    public ResultVO<Teacher> AddTeacher(Teacher teacher);
    public ResultVO<Teacher> UpdateTeacher(Teacher teacher);
    public ResultVO<Teacher> DeleteTeacher(Teacher teacher);
    public ResultVO<Teacher> GetTeacher(Teacher teacher);
    public ResultVO<List> GetTeacherList(Teacher teacher);
    public ResultVO<List> GetTeacherListByPage(Teacher teacher, int pageNum, int pageSize);
    // 老师登录
    public ResultVO<Teacher> TeacherLogin(Teacher teacher);
    // 重置教师密码
    public ResultVO<Teacher> ResetTeacherPassword(Integer teachId, String newPassword);
}
