package com.jeein.member.dto.response;

import com.jeein.member.entity.Manager;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ManagerJoinResponseDTO {
    private String id;
    private String name;
    private String nickname;
    private String email;

    public static ManagerJoinResponseDTO fromEntity(Manager manager) {
        return ManagerJoinResponseDTO.builder()
                .id(manager.getId().toString())
                .name(manager.getName())
                .nickname(manager.getNickname())
                .email(manager.getEmail())
                .build();
    }
}
