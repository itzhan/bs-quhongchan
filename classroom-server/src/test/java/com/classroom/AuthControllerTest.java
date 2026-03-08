package com.classroom;

import com.classroom.controller.AuthController;
import com.classroom.dto.LoginDTO;
import com.classroom.service.SysUserService;
import com.classroom.vo.LoginVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SysUserService sysUserService;

    /**
     * 测试1: 登录成功
     */
    @Test
    void testLoginSuccess() throws Exception {
        LoginVO vo = new LoginVO();
        vo.setToken("mock-jwt-token");
        vo.setUserId(1L);
        vo.setUsername("admin");
        vo.setRealName("系统管理员");
        vo.setRole("ADMIN");

        Mockito.when(sysUserService.login(any(LoginDTO.class))).thenReturn(vo);

        LoginDTO dto = new LoginDTO();
        dto.setUsername("admin");
        dto.setPassword("admin123");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.token").value("mock-jwt-token"))
                .andExpect(jsonPath("$.data.role").value("ADMIN"));
    }

    /**
     * 测试2: 登录参数校验 - 用户名为空
     */
    @Test
    void testLoginValidation() throws Exception {
        LoginDTO dto = new LoginDTO();
        dto.setUsername("");
        dto.setPassword("admin123");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400));
    }

    /**
     * 测试3: 登录失败 - 密码错误
     */
    @Test
    void testLoginBadCredentials() throws Exception {
        Mockito.when(sysUserService.login(any(LoginDTO.class)))
                .thenThrow(new BadCredentialsException("用户名或密码错误"));

        LoginDTO dto = new LoginDTO();
        dto.setUsername("admin");
        dto.setPassword("wrongpassword");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401));
    }

    /**
     * 测试4: 未登录访问受保护接口
     */
    @Test
    void testUnauthorizedAccess() throws Exception {
        mockMvc.perform(get("/user/info"))
                .andExpect(status().isUnauthorized());
    }
}
