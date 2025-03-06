package com.jeein.member.dto.response;

import com.jeein.member.entity.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetMemberResponseDTO {
    private String id;
    private String nickname;
    private String email;
    private String name;

    public static GetMemberResponseDTO fromEntity(Member member) {
        return GetMemberResponseDTO.builder()
                .id(member.getId().toString())
                .nickname(member.getNickname())
                .email(member.getEmail())
                .name(member.getName())
                .build();
    }
}
