package com.example.zhjypt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.zhjypt.pojo.Admin;
import com.example.zhjypt.mapper.AdminMapper;
import com.example.zhjypt.service.AdminService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.zhjypt.vo.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.zhjypt.utils.PasswordUtil;

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
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {
    @Autowired
    private AdminMapper adminMapper;

    @Override
    public ResultVO<Admin> AdminLogin(Admin admin) {
        if(admin.getAdmAccount() == null||admin.getAdmAccount().isEmpty()){
            return ResultVO.fail("登录失败：账号不能为空");
        }
        if(admin.getAdmPassword() == null||admin.getAdmPassword().isEmpty()){
            return ResultVO.fail("登录失败：密码不能为空");
        }
        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("adm_account", admin.getAdmAccount());
        Admin currentAdmin = adminMapper.selectOne(queryWrapper);
        if(currentAdmin == null){
            return ResultVO.fail("账号不存在");
        }else {
            boolean matched = PasswordUtil.matches(admin.getAdmPassword(), currentAdmin.getAdmPassword());
            if (matched) {
                if (!PasswordUtil.isBCryptHash(currentAdmin.getAdmPassword())) {
                    UpdateWrapper<Admin> upgradeWrapper = new UpdateWrapper<>();
                    upgradeWrapper.eq("adm_id", currentAdmin.getAdmId());
                    upgradeWrapper.set("adm_password", PasswordUtil.hashIfNeeded(admin.getAdmPassword()));
                    this.update(null, upgradeWrapper);
                }
                return ResultVO.success("登录成功", currentAdmin);
            } else {
                return ResultVO.fail("密码错误");
            }
        }
    }

    @Override
    public ResultVO<Admin> AddAdmin(Admin admin) {
        // 默认：把入参密码转成 BCrypt（避免明文入库）
        admin.setAdmPassword(PasswordUtil.hashIfNeeded(admin.getAdmPassword()));
        this.save(admin);
        return ResultVO.success("添加成功",admin);
    }

    @Override
    public ResultVO<Admin> UpdateAdmin(Admin admin) {
        // 1. 校验主键是否存在（必须传入adm_id才能定位要修改的记录）
        if (admin.getAdmId() == null) {
            return ResultVO.fail("更新失败：必须传入adm_id");
        }

        UpdateWrapper<Admin> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("adm_id", admin.getAdmId()); // 定位要修改的记录

        // 2. 动态判断每个字段是否有值，有值才添加set条件
        // 处理adm_name：非null且非空字符串才更新
        if (admin.getAdmName() != null && !admin.getAdmName().trim().isEmpty()) {
            updateWrapper.set("adm_name", admin.getAdmName());
        }
        // 处理adm_account：非null且非空才更新
        if (admin.getAdmAccount() != null && !admin.getAdmAccount().trim().isEmpty()) {
            updateWrapper.set("adm_account", admin.getAdmAccount());
        }
        // 处理adm_password：非null且非空才更新
        if (admin.getAdmPassword() != null && !admin.getAdmPassword().trim().isEmpty()) {
            updateWrapper.set("adm_password", PasswordUtil.hashIfNeeded(admin.getAdmPassword()));
        }
        // 处理adm_phone：非null且非空才更新
        if (admin.getAdmPhone() != null && !admin.getAdmPhone().trim().isEmpty()) {
            updateWrapper.set("adm_phone", admin.getAdmPhone());
        }
        // 处理adm_email：非null且非空才更新
        if (admin.getAdmEmail() != null && !admin.getAdmEmail().trim().isEmpty()) {
            updateWrapper.set("adm_email", admin.getAdmEmail());
        }
        // 处理adm_address：非null且非空才更新
        if (admin.getAdmAddress() != null && !admin.getAdmAddress().trim().isEmpty()) {
            updateWrapper.set("adm_address", admin.getAdmAddress());
        }
        // 处理avatar：非null才更新
        if (admin.getAvatar() != null) {
            updateWrapper.set("avatar", admin.getAvatar());
        }

        // 3. 执行更新（若没有任何set条件，会返回false）
        boolean isUpdate = this.update(null, updateWrapper);
        if (isUpdate) {
            // 可选：更新成功后查询最新数据返回（避免返回的admin是入参的旧值）
            Admin updatedAdmin = this.getById(admin.getAdmId());
            return ResultVO.success("更新成功", updatedAdmin);
        } else {
            return ResultVO.fail("更新失败：无匹配记录或未传入任何修改字段");
        }
    }

    @Override
    public ResultVO<Admin> DeleteAdmin(Admin admin) {
        if (admin.getAdmId() == null) {
            return ResultVO.fail("逻辑删除失败：必须传入adm_id");
        }
        Admin delAdmin = this.getById(admin.getAdmId());
        // 调用MyBatis-Plus的removeById方法，会自动执行逻辑删除（无需手动构建UpdateWrapper）
        boolean isLogicDelete = this.removeById(admin.getAdmId());
        if (isLogicDelete) {
            return ResultVO.success("逻辑删除成功", delAdmin);
        } else {
            return ResultVO.fail("逻辑删除失败：无匹配记录或该记录已被删除");
        }
    }

    @Override
    public ResultVO<Admin> GetAdmin(Admin admin) {
        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del", 0);
        if (admin.getAdmId() != null) {
            queryWrapper.eq("adm_id", admin.getAdmId());
        }
        Admin currentAdmin = this.getOne(queryWrapper);
        if (currentAdmin != null) {
            return ResultVO.success("查询成功", currentAdmin);
        } else {
            return ResultVO.fail("无数据");
        }
    }

    @Override
    public ResultVO<List> GetAdminList(Admin admin) {
        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del", 0);
        if (admin.getAdmName() != null) {
            queryWrapper.like("adm_name", admin.getAdmName());
        }
        List<Admin> adminList = this.list(queryWrapper);
        if(adminList==null||adminList.isEmpty()){
            return ResultVO.fail("无数据");
        }else{
            return ResultVO.success("查询成功", adminList);
        }
    }
    @Override
    public ResultVO<List> GetAdminListByPage(Admin admin, int pageNum, int pageSize) {
        Page<Admin> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del", 0);
        Page<Admin> adminPage = adminMapper.selectPage(page, queryWrapper);
        List<Admin> adminList = adminPage.getRecords();
        long total = adminPage.getTotal();
        if(adminList==null||adminList.isEmpty()){
            return ResultVO.fail("无数据");
        }else{
            return ResultVO.success("查询成功", adminList,total);
        }
    }
}
