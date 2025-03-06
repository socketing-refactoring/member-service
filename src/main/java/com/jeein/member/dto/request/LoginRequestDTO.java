package com.jeein.member.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class LoginRequestDTO {
    @NotEmpty(message = "이메일은 필수입니다.")
    @Email
    @Size(min = 1, max = 20, message = "이메일은 1자 이상 20자 이하로 입력해 주세요.")
    private String email;

    @NotEmpty(message = "비밀번호는 필수입니다.")
    @Size(min = 1, max = 20, message = "비밀번호는 1자 이상 20자 이하로 입력해 주세요.")
    private String password;
}
