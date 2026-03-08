package com.classroom.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.classroom.common.result.R;
import com.classroom.entity.SysUser;
import com.classroom.service.SysUserService;
import com.classroom.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class SysUserController {

    private final SysUserService sysUserService;

    @GetMapping("/info")
    public R<SysUser> getCurrentUser() {
        SysUser user = sysUserService.getById(SecurityUtil.getCurrentUserId());
        if (user != null) user.setPassword(null);
        return R.ok(user);
    }

    @PutMapping("/profile")
    public R<Void> updateProfile(@RequestBody SysUser user) {
        user.setId(SecurityUtil.getCurrentUserId());
        sysUserService.updateProfile(user);
        return R.ok();
    }

    @GetMapping("/page")
    @PreAuthorize("hasRole('ADMIN')")
    public R<IPage<SysUser>> page(@RequestParam(defaultValue = "1") int page,
                                   @RequestParam(defaultValue = "10") int size,
                                   @RequestParam(required = false) String keyword,
                                   @RequestParam(required = false) String role,
                                   @RequestParam(required = false) Integer status) {
        return R.ok(sysUserService.pageList(page, size, keyword, role, status));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public R<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        sysUserService.updateStatus(id, status);
        return R.ok();
    }

    @PutMapping("/{id}/reset-password")
    @PreAuthorize("hasRole('ADMIN')")
    public R<Void> resetPassword(@PathVariable Long id, @RequestParam String newPassword) {
        sysUserService.resetPassword(id, newPassword);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public R<Void> delete(@PathVariable Long id) {
        sysUserService.removeById(id);
        return R.ok();
    }
}
