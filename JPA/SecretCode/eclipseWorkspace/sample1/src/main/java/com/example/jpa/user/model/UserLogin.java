package com.example.jpa.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLogin {

    @NotBlank(message = "이메일 항목은 필수입니다.")
    private String email;

    @NotBlank(message = "이메일 항목은 필수 입니다.")
    private String password;
}
