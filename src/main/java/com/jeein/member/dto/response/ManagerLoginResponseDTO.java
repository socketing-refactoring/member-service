package com.jeein.member.dto.response;

import com.jeein.member.entity.Manager;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ManagerLoginResponseDTO {
    private String id;
    private String email;
    private String name;
    private String nickname;

    public static ManagerLoginResponseDTO fromEntity(Manager manager) {
        return ManagerLoginResponseDTO.builder()
                .id(manager.getId().toString())
                .email(manager.getEmail())
                .name(manager.getName())
                .nickname(manager.getNickname())
                .build();
    }
}
