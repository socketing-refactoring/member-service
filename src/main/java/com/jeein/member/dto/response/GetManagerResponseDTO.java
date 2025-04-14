package com.jeein.member.dto.response;

import com.jeein.member.entity.Manager;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetManagerResponseDTO {
    private String id;
    private String nickname;
    private String email;
    private String name;

    public static GetManagerResponseDTO fromEntity(Manager manager) {
        return GetManagerResponseDTO.builder()
                .id(manager.getId().toString())
                .nickname(manager.getNickname())
                .email(manager.getEmail())
                .name(manager.getName())
                .build();
    }
}
