package com.jeein.member.dto.response;

import com.jeein.member.entity.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponseDTO {
    private String id;
    private String email;
    private String name;
    private String nickname;

    public static LoginResponseDTO fromEntity(Member member) {
        return LoginResponseDTO.builder()
                .id(member.getId().toString())
                .email(member.getEmail())
                .name(member.getName())
                .nickname(member.getNickname())
                .build();
    }
}
