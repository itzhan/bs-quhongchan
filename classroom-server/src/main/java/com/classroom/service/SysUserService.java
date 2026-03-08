package com.classroom.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.classroom.dto.LoginDTO;
import com.classroom.dto.RegisterDTO;
import com.classroom.entity.SysUser;
import com.classroom.vo.LoginVO;

public interface SysUserService extends IService<SysUser> {
    LoginVO login(LoginDTO dto);
    void register(RegisterDTO dto);
    IPage<SysUser> pageList(int page, int size, String keyword, String role, Integer status);
    void updateStatus(Long id, Integer status);
    void resetPassword(Long id, String newPassword);
    void updateProfile(SysUser user);
}
