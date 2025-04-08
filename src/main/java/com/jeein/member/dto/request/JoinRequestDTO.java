package com.jeein.member.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class JoinRequestDTO {

    @NotEmpty(message = "이메일은 필수입니다.")
    @Email(message = "이메일을 형식에 맞게 입력해 주세요.")
    @Size(max = 50, message = "이메일을 50자 이하로 입력해 주세요.")
    private String email;

    @NotEmpty(message = "이름은 필수입니다.")
    @Size(max = 20, message = "이름을 20자 이하로 입력해 주세요.")
    private String name;

    @NotEmpty(message = "닉네임은 필수입니다.")
    @Size(max = 20, message = "닉네임을 20자 이하로 입력해 주세요.")
    private String nickname;

    @NotEmpty(message = "비밀번호는 필수입니다.")
    @Size(max = 20, message = "비밀번호를 20자 이하로 입력해 주세요.")
    private String password;

    public static JoinRequestDTO of(String email, String name, String nickname, String password) {
        return JoinRequestDTO.builder()
                .email(email)
                .name(name)
                .nickname(nickname)
                .password(password)
                .build();
    }
}
