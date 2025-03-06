package com.jeein.member.dto.response;

import com.jeein.member.entity.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponseDTO {
    private String id;
    private String nickname;
    private String email;

    public static LoginResponseDTO fromEntity(Member member) {
        return LoginResponseDTO.builder()
                .id(member.getId().toString())
                .nickname(member.getNickname())
                .email(member.getEmail())
                .build();
    }
}
