package com.jeein.member.controller;

import com.jeein.member.dto.common.CommonResponseDTO;
import com.jeein.member.dto.request.JoinRequestDTO;
import com.jeein.member.dto.request.LoginRequestDTO;
import com.jeein.member.dto.request.UpdateNicknameRequestDTO;
import com.jeein.member.dto.request.UpdatePasswordRequestDTO;
import com.jeein.member.dto.response.GetManagerResponseDTO;
import com.jeein.member.dto.response.ManagerJoinResponseDTO;
import com.jeein.member.dto.response.ManagerLoginResponseDTO;
import com.jeein.member.dto.response.UpdateManagerNicknameResponseDTO;
import com.jeein.member.service.ManagerService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/managers/managers")
@RequiredArgsConstructor
public class ManagerController {

    private final ManagerService managerService;

    // 모든 회원 목록 조회
    @GetMapping
    public ResponseEntity<CommonResponseDTO<List<GetManagerResponseDTO>>> getAllManagers() {
        return ResponseEntity.ok(managerService.getAllManagers());
    }

    // 특정 회원 조회
    @GetMapping("/{id}")
    public ResponseEntity<CommonResponseDTO<GetManagerResponseDTO>> getManager(
            @PathVariable String id) {
        return ResponseEntity.ok(managerService.getManagerById(id));
    }

    // 닉네임 업데이트
    @PatchMapping("/{id}/nickname")
    public ResponseEntity<CommonResponseDTO<UpdateManagerNicknameResponseDTO>> updateNickname(
            @PathVariable String id,
            @Valid @RequestBody UpdateNicknameRequestDTO updateNicknameRequest) {
        return ResponseEntity.ok(managerService.updateNickname(id, updateNicknameRequest));
    }

    // 비밀번호 업데이트
    @PatchMapping("/{id}/password")
    public ResponseEntity<CommonResponseDTO<Void>> updatePassword(
            @PathVariable String id,
            @Valid @RequestBody UpdatePasswordRequestDTO updatePasswordRequest) {
        return ResponseEntity.ok(managerService.updatePassword(id, updatePasswordRequest));
    }

    // 회원 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<CommonResponseDTO<Void>> deleteManager(@PathVariable String id) {
        return ResponseEntity.ok(managerService.deleteManager(id));
    }

    /*----------------------------------
     * ManagerFeignClient
    ----------------------------------*/
    // 새로운 회원 등록
    @PostMapping("/join")
    public ResponseEntity<CommonResponseDTO<ManagerJoinResponseDTO>> joinManager(
            @Valid @RequestBody JoinRequestDTO joinRequestDTO) {
        CommonResponseDTO<ManagerJoinResponseDTO> response =
                managerService.joinManager(joinRequestDTO);

        return ResponseEntity.status(HttpStatus.CREATED)
                .location(URI.create("/api/v1/members/managers/" + response.getData().getId()))
                .body(response);
    }

    // 회원 로그인 인증
    @PostMapping("/login")
    public ResponseEntity<CommonResponseDTO<ManagerLoginResponseDTO>> loginRequestDTO(
            @Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        return ResponseEntity.ok(managerService.loginManager(loginRequestDTO));
    }
}
