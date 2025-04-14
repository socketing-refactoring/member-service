package com.jeein.member.dto.response;

import com.jeein.member.entity.Manager;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateManagerNicknameResponseDTO {
    private String id;
    private String nickname;

    public static UpdateManagerNicknameResponseDTO fromEntity(Manager manager) {
        return UpdateManagerNicknameResponseDTO.builder()
                .id(manager.getId().toString())
                .nickname(manager.getNickname())
                .build();
    }
}
