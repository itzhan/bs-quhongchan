package com.classroom.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class RegisterDTO {
    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    @NotBlank(message = "真实姓名不能为空")
    private String realName;

    private String email;
    private String phone;
    private Integer gender;

    @NotBlank(message = "角色不能为空")
    @Pattern(regexp = "STUDENT|TEACHER", message = "角色只能是STUDENT或TEACHER")
    private String role;
}
