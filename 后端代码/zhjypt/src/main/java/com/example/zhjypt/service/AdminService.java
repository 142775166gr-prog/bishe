package com.example.zhjypt.service;

import com.example.zhjypt.pojo.Admin;
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
public interface AdminService extends IService<Admin> {
    public ResultVO<Admin> AddAdmin(Admin admin);
    public ResultVO<Admin> UpdateAdmin(Admin admin);
    public ResultVO<Admin> DeleteAdmin(Admin admin);
    public ResultVO<Admin> GetAdmin(Admin admin);
    public ResultVO<List> GetAdminList(Admin admin);
    public ResultVO<List> GetAdminListByPage(Admin admin,int pageNum,int pageSize);

    ResultVO<Admin> AdminLogin(Admin admin);
}
