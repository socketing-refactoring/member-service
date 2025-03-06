package com.jeein.member.dto.response;

import com.jeein.member.entity.Member;
import lombok.Builder;

@Builder
public class UpdateNicknameResponseDTO {
    private String id;
    private String nickname;

    public static UpdateNicknameResponseDTO fromEntity(Member member) {
        return UpdateNicknameResponseDTO.builder()
                .id(member.getId().toString())
                .nickname(member.getNickname())
                .build();
    }
}
