package com.example.zhjypt.controller;


import com.example.zhjypt.pojo.Admin;
import com.example.zhjypt.service.AdminService;
import com.example.zhjypt.vo.ResultVO;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
@RequestMapping("/Admin")
@CrossOrigin
public class AdminController {
    @Autowired
    private AdminService adminService;

    @GetMapping("/AddAdmin")
    @ApiOperation(value = "添加管理员",notes = "添加管理员")
    public ResultVO<Admin> AddAdmin(Admin admin){
        return adminService.AddAdmin(admin);
    }
    @GetMapping("/UpdateAdmin")
    @ApiOperation(value = "修改管理员",notes = "修改管理员")
    public ResultVO<Admin> UpdateAdmin(Admin admin){
        return adminService.UpdateAdmin(admin);
    }
    @GetMapping("/DeleteAdmin")
    @ApiOperation(value = "删除管理员",notes = "删除管理员")
    public ResultVO<Admin> DeleteAdmin(Admin admin){
        return adminService.DeleteAdmin(admin);
    }
    @GetMapping("/GetAdmin")
    @ApiOperation(value = "获取管理员",notes = "获取管理员")
    public ResultVO<Admin> GetAdmin(Admin admin){
        return adminService.GetAdmin(admin);
    }
    @GetMapping("/GetAdminList")
    @ApiOperation(value = "获取管理员列表",notes = "获取管理员列表")
    public ResultVO<List> GetAdminList(Admin admin){
        return adminService.GetAdminList(admin);
    }
    @GetMapping("/GetAdminListByPage")
    @ApiOperation(value = "分页获取管理员列表",notes = "分页获取管理员列表")
    public ResultVO<List> GetAdminListByPage(Admin admin,int pageNum,int pageSize){
        return adminService.GetAdminListByPage(admin,pageNum,pageSize);
    }

    @GetMapping("/AdminLogin")
    @ApiOperation(value = "管理员登录",notes = "管理员登录")
    public ResultVO<Admin> AdminLogin(Admin admin) {
        return adminService.AdminLogin(admin);
    }
}
