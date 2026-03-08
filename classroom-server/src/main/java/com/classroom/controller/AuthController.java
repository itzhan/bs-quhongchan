package com.classroom.controller;

import com.classroom.common.result.R;
import com.classroom.dto.LoginDTO;
import com.classroom.dto.RegisterDTO;
import com.classroom.service.SysUserService;
import com.classroom.vo.LoginVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final SysUserService sysUserService;

    @PostMapping("/login")
    public R<LoginVO> login(@Valid @RequestBody LoginDTO dto) {
        return R.ok(sysUserService.login(dto));
    }

    @PostMapping("/register")
    public R<Void> register(@Valid @RequestBody RegisterDTO dto) {
        sysUserService.register(dto);
        return R.ok();
    }
}
