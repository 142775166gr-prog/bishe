package com.example.zhjypt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.zhjypt.pojo.Teacher;
import com.example.zhjypt.mapper.TeacherMapper;
import com.example.zhjypt.service.TeacherService;
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
public class TeacherServiceImpl extends ServiceImpl<TeacherMapper, Teacher> implements TeacherService {
    @Autowired
    private TeacherMapper teacherMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public ResultVO<Teacher> AddTeacher(Teacher teacher) {
        // 默认：把入参密码转成 BCrypt（避免明文入库）
        teacher.setTeachPassword(PasswordUtil.hashIfNeeded(teacher.getTeachPassword()));
        this.save(teacher);
        return ResultVO.success("添加成功", teacher);
    }

    @Override
    public ResultVO<Teacher> UpdateTeacher(Teacher teacher) {
        // 1. 校验主键是否存在（必须传入teach_id才能定位要修改的记录）
        if (teacher.getTeachId() == null) {
            return ResultVO.fail("更新失败：必须传入teach_id");
        }

        UpdateWrapper<Teacher> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("teach_id", teacher.getTeachId()); // 定位要修改的记录

        // 2. 动态判断每个字段是否有值，有值才添加set条件
        // 处理teach_name：非null且非空字符串才更新
        if (teacher.getTeachName() != null && !teacher.getTeachName().trim().isEmpty()) {
            updateWrapper.set("teach_name", teacher.getTeachName());
        }
        // 处理teach_account：非null且非空才更新
        if (teacher.getTeachAccount() != null && !teacher.getTeachAccount().trim().isEmpty()) {
            updateWrapper.set("teach_account", teacher.getTeachAccount());
        }
        // 处理teach_password：非null且非空才更新
        if (teacher.getTeachPassword() != null && !teacher.getTeachPassword().trim().isEmpty()) {
            updateWrapper.set("teach_password", PasswordUtil.hashIfNeeded(teacher.getTeachPassword()));
        }
        // 处理teach_phone：非null且非空才更新
        if (teacher.getTeachPhone() != null && !teacher.getTeachPhone().trim().isEmpty()) {
            updateWrapper.set("teach_phone", teacher.getTeachPhone());
        }
        // 处理teach_email：非null且非空才更新
        if (teacher.getTeachEmail() != null && !teacher.getTeachEmail().trim().isEmpty()) {
            updateWrapper.set("teach_email", teacher.getTeachEmail());
        }
        // 处理teach_address：非null且非空才更新
        if (teacher.getTeachAddress() != null && !teacher.getTeachAddress().trim().isEmpty()) {
            updateWrapper.set("teach_address", teacher.getTeachAddress());
        }
        // 处理avatar：非null才更新
        if (teacher.getAvatar() != null) {
            updateWrapper.set("avatar", teacher.getAvatar());
        }
        // 3. 执行更新（若没有任何set条件，会返回false）
        boolean isUpdate = this.update(null, updateWrapper);
        if (isUpdate) {
            // 可选：更新成功后查询最新数据返回（避免返回的teacher是入参的旧值）
            Teacher updatedTeacher = this.getById(teacher.getTeachId());
            return ResultVO.success("更新成功", updatedTeacher);
        } else {
            return ResultVO.fail("更新失败：无匹配记录或未传入任何修改字段");
        }
    }

    @Override
    public ResultVO<Teacher> DeleteTeacher(Teacher teacher) {
        if (teacher.getTeachId() == null) {
            return ResultVO.fail("逻辑删除失败：必须传入teach_id");
        }
        Teacher delTeacher = this.getById(teacher.getTeachId());
        // 调用MyBatis-Plus的removeById方法，会自动执行逻辑删除（无需手动构建UpdateWrapper）
        boolean isLogicDelete = this.removeById(teacher.getTeachId());
        if (isLogicDelete) {
            return ResultVO.success("逻辑删除成功", delTeacher);
        } else {
            return ResultVO.fail("逻辑删除失败：无匹配记录或该记录已被删除");
        }
    }

    @Override
    public ResultVO<Teacher> GetTeacher(Teacher teacher) {
        QueryWrapper<Teacher> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del", 0);
        if (teacher.getTeachId() != null) {
            queryWrapper.eq("teach_id", teacher.getTeachId());
        }
        Teacher currentTeacher = this.getOne(queryWrapper);
        if (currentTeacher != null) {
            return ResultVO.success("查询成功", currentTeacher);
        } else {
            return ResultVO.fail("无数据");
        }
    }

    @Override
    public ResultVO<List> GetTeacherList(Teacher teacher) {
        QueryWrapper<Teacher> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del", 0);
        if (teacher.getTeachName() != null) {
            queryWrapper.like("teach_name", teacher.getTeachName());
        }
        List<Teacher> teacherList = this.list(queryWrapper);
        if(teacherList==null||teacherList.isEmpty()){
            return ResultVO.fail("无数据");
        }else{
            return ResultVO.success("查询成功", teacherList);
        }
    }

    @Override
    public ResultVO<List> GetTeacherListByPage(Teacher teacher, int pageNum, int pageSize) {
        Page<Teacher> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Teacher> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del", 0);
        // 支持按姓名或账号模糊查询
        if (teacher.getTeachName() != null && !teacher.getTeachName().trim().isEmpty()) {
            queryWrapper.like("teach_name", teacher.getTeachName());
        }
        if (teacher.getTeachAccount() != null && !teacher.getTeachAccount().trim().isEmpty()) {
            queryWrapper.like("teach_account", teacher.getTeachAccount());
        }
        Page<Teacher> teacherPage = teacherMapper.selectPage(page, queryWrapper);
        List<Teacher> teacherList = teacherPage.getRecords();
        long total = teacherPage.getTotal();
        if(teacherList==null||teacherList.isEmpty()){
            return ResultVO.fail("无数据");
        }else{
            return ResultVO.success("查询成功", teacherList,total);
        }
    }

    @Override
    public ResultVO<Teacher> TeacherLogin(Teacher teacher) {
        if(teacher.getTeachAccount() == null||teacher.getTeachAccount().isEmpty()){
            return ResultVO.fail("登录失败：账号不能为空");
        }
        if(teacher.getTeachPassword() == null||teacher.getTeachPassword().isEmpty()){
            return ResultVO.fail("登录失败：密码不能为空");
        }
        QueryWrapper<Teacher> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("teach_account", teacher.getTeachAccount());
        Teacher current = teacherMapper.selectOne(queryWrapper);
        if(current == null){
            return ResultVO.fail("账号不存在");
        }else {
            boolean matched = PasswordUtil.matches(teacher.getTeachPassword(), current.getTeachPassword());
            if (matched) {
                if (!PasswordUtil.isBCryptHash(current.getTeachPassword())) {
                    UpdateWrapper<Teacher> upgradeWrapper = new UpdateWrapper<>();
                    upgradeWrapper.eq("teach_id", current.getTeachId());
                    upgradeWrapper.set("teach_password", PasswordUtil.hashIfNeeded(teacher.getTeachPassword()));
                    this.update(null, upgradeWrapper);
                }
                current.setToken(jwtUtil.generateToken("teacher", current.getTeachId()));
                return ResultVO.success("登录成功", current);
            } else {
                return ResultVO.fail("密码错误");
            }
        }
    }

    @Override
    public ResultVO<Teacher> ResetTeacherPassword(Integer teachId, String newPassword) {
        if (teachId == null) {
            return ResultVO.fail("重置密码失败：必须传入teach_id");
        }
        if (newPassword == null || newPassword.trim().isEmpty()) {
            return ResultVO.fail("重置密码失败：新密码不能为空");
        }
        UpdateWrapper<Teacher> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("teach_id", teachId);
        updateWrapper.set("teach_password", PasswordUtil.hashIfNeeded(newPassword));
        boolean updated = this.update(null, updateWrapper);
        if (updated) {
            Teacher updatedTeacher = this.getById(teachId);
            return ResultVO.success("密码已重置", updatedTeacher);
        } else {
            return ResultVO.fail("重置密码失败：无匹配记录");
        }
    }
}
