package com.example.zhjypt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.zhjypt.pojo.Student;
import com.example.zhjypt.mapper.StudentMapper;
import com.example.zhjypt.service.StudentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.zhjypt.vo.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.zhjypt.utils.PasswordUtil;
import com.example.zhjypt.security.JwtUtil;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author daswswsdgkj
 * @since 2025-12-22
 */
@Service
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements StudentService {
    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public ResultVO<Student> AddStudent(Student student) {
        // 基本校验
        if (student.getStuAccount() == null || student.getStuAccount().trim().isEmpty()) {
            return ResultVO.fail("注册失败：账号不能为空");
        }

        // 1. 检查账号是否已存在（不区分是否逻辑删除，整个表保持唯一）
        QueryWrapper<Student> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("stu_account", student.getStuAccount().trim());
        Integer count = studentMapper.selectCount(queryWrapper);
        if (count != null && count > 0) {
            return ResultVO.fail("注册失败：该账号已存在，请更换一个账号");
        }

        // 2. 保存新学生
        // 默认：把入参密码转成 BCrypt（避免明文入库）
        student.setStuPassword(PasswordUtil.hashIfNeeded(student.getStuPassword()));
        boolean saved = this.save(student);
        if (saved) {
            return ResultVO.success("添加成功", student);
        } else {
            return ResultVO.fail("注册失败：保存数据时出现错误");
        }
    }

    @Override
    public ResultVO<Student> UpdateStudent(Student student) {
        // 1. 校验主键是否存在（必须传入stu_id才能定位要修改的记录）
        if (student.getStuId() == null) {
            return ResultVO.fail("更新失败：必须传入stu_id");
        }

        UpdateWrapper<Student> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("stu_id", student.getStuId()); // 定位要修改的记录

        // 2. 动态判断每个字段是否有值，有值才添加set条件
        // 处理stu_name：非null且非空字符串才更新
        if (student.getStuName() != null && !student.getStuName().trim().isEmpty()) {
            updateWrapper.set("stu_name", student.getStuName());
        }
        // 处理stu_account：非null且非空才更新
        if (student.getStuAccount() != null && !student.getStuAccount().trim().isEmpty()) {
            updateWrapper.set("stu_account", student.getStuAccount());
        }
        // 处理stu_password：非null且非空才更新
        if (student.getStuPassword() != null && !student.getStuPassword().trim().isEmpty()) {
            updateWrapper.set("stu_password", PasswordUtil.hashIfNeeded(student.getStuPassword()));
        }
        // 处理stu_phone：非null且非空才更新
        if (student.getStuPhone() != null && !student.getStuPhone().trim().isEmpty()) {
            updateWrapper.set("stu_phone", student.getStuPhone());
        }
        // 处理stu_email：非null且非空才更新
        if (student.getStuEmail() != null && !student.getStuEmail().trim().isEmpty()) {
            updateWrapper.set("stu_email", student.getStuEmail());
        }
        // 处理stu_university：非null且非空才更新
        if (student.getStuUniversity() != null && !student.getStuUniversity().trim().isEmpty()) {
            updateWrapper.set("stu_university", student.getStuUniversity());
        }
        // 处理avatar：非null才更新（允许空字符串清除头像）
        if (student.getAvatar() != null) {
            updateWrapper.set("avatar", student.getAvatar());
        }
        // 3. 执行更新（若没有任何set条件，会返回false）
        boolean isUpdate = this.update(null, updateWrapper);
        if (isUpdate) {
            // 可选：更新成功后查询最新数据返回（避免返回的student是入参的旧值）
            Student updatedStudent = this.getById(student.getStuId());
            return ResultVO.success("更新成功", updatedStudent);
        } else {
            return ResultVO.fail("更新失败：无匹配记录或未传入任何修改字段");
        }
    }

    @Override
    public ResultVO<Student> DeleteStudent(Student student) {
        if (student.getStuId() == null) {
            return ResultVO.fail("逻辑删除失败：必须传入stu_id");
        }
        Student delStudent = this.getById(student.getStuId());
        // 调用MyBatis-Plus的removeById方法，会自动执行逻辑删除（无需手动构建UpdateWrapper）
        boolean isLogicDelete = this.removeById(student.getStuId());
        if (isLogicDelete) {
            return ResultVO.success("逻辑删除成功", delStudent);
        } else {
            return ResultVO.fail("逻辑删除失败：无匹配记录或该记录已被删除");
        }
    }

    @Override
    public ResultVO<Student> GetStudent(Student student) {
        QueryWrapper<Student> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del", 0);
        if (student.getStuId() != null) {
            queryWrapper.eq("stu_id", student.getStuId());
        }
        Student currentStudent = this.getOne(queryWrapper);
        if (currentStudent != null) {
            return ResultVO.success("查询成功", currentStudent);
        } else {
            return ResultVO.fail("无数据");
        }
    }

    @Override
    public ResultVO<List> GetStudentList(Student student) {
        QueryWrapper<Student> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del", 0);
        if (student.getStuName() != null) {
            queryWrapper.like("stu_name", student.getStuName());
        }
        List<Student> studentList = this.list(queryWrapper);
        if(studentList==null||studentList.isEmpty()){
            return ResultVO.fail("无数据");
        }else{
            return ResultVO.success("查询成功", studentList);
        }
    }

    @Override
    public ResultVO<List> GetStudentListByPage(Student student, int pageNum, int pageSize) {
        Page<Student> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Student> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del", 0);
        // 支持按姓名或账号模糊查询
        if (student.getStuName() != null && !student.getStuName().trim().isEmpty()) {
            queryWrapper.like("stu_name", student.getStuName());
        }
        if (student.getStuAccount() != null && !student.getStuAccount().trim().isEmpty()) {
            queryWrapper.like("stu_account", student.getStuAccount());
        }
        Page<Student> studentPage = studentMapper.selectPage(page, queryWrapper);
        List<Student> studentList = studentPage.getRecords();
        long total = studentPage.getTotal();
        if(studentList==null||studentList.isEmpty()){
            return ResultVO.fail("无数据");
        }else{
            return ResultVO.success("查询成功", studentList,total);
        }
    }

    @Override
    public ResultVO<Student> StudentLogin(Student student) {
        if(student.getStuAccount() == null||student.getStuAccount().isEmpty()){
            return ResultVO.fail("登录失败：账号不能为空");
        }
        if(student.getStuPassword() == null||student.getStuPassword().isEmpty()){
            return ResultVO.fail("登录失败：密码不能为空");
        }
        QueryWrapper<Student> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("stu_account", student.getStuAccount());
        Student current = studentMapper.selectOne(queryWrapper);
        if(current == null){
            return ResultVO.fail("账号不存在");
        }else {
            boolean matched = PasswordUtil.matches(student.getStuPassword(), current.getStuPassword());
            if (matched) {
                // 如果历史库里是明文，则登录成功后顺便升级成 BCrypt
                if (!PasswordUtil.isBCryptHash(current.getStuPassword())) {
                    UpdateWrapper<Student> upgradeWrapper = new UpdateWrapper<>();
                    upgradeWrapper.eq("stu_id", current.getStuId());
                    upgradeWrapper.set("stu_password", PasswordUtil.hashIfNeeded(student.getStuPassword()));
                    this.update(null, upgradeWrapper);
                }
                // 登录成功后生成 JWT token
                current.setToken(jwtUtil.generateToken("student", current.getStuId()));
                return ResultVO.success("登录成功", current);
            } else {
                return ResultVO.fail("密码错误");
            }
        }
    }

    @Override
    public ResultVO<Student> ResetStudentPassword(Integer stuId, String newPassword) {
        if (stuId == null) {
            return ResultVO.fail("重置密码失败：必须传入stu_id");
        }
        if (newPassword == null || newPassword.trim().isEmpty()) {
            return ResultVO.fail("重置密码失败：新密码不能为空");
        }
        UpdateWrapper<Student> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("stu_id", stuId);
        updateWrapper.set("stu_password", PasswordUtil.hashIfNeeded(newPassword));
        boolean updated = this.update(null, updateWrapper);
        if (updated) {
            Student updatedStudent = this.getById(stuId);
            return ResultVO.success("密码已重置", updatedStudent);
        } else {
            return ResultVO.fail("重置密码失败：无匹配记录");
        }
    }
}
