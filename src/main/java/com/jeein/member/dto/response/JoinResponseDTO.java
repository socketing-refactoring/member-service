package com.jeein.member.dto.response;

import com.jeein.member.entity.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class JoinResponseDTO {
    private String id;
    private String nickname;
    private String email;

    public static JoinResponseDTO fromEntity(Member member) {
        return JoinResponseDTO.builder()
                .id(member.getId().toString())
                .nickname(member.getNickname())
                .email(member.getEmail())
                .build();
    }
}
