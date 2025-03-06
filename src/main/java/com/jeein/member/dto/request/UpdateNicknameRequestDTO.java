package com.jeein.member.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UpdateNicknameRequestDTO {
    @NotEmpty(message = "닉네임은 필수입니다.")
    @Size(min = 1, max = 20, message = "닉네임은 1자 이상 20자 이하로 입력해 주세요.")
    private String nickname;
}
