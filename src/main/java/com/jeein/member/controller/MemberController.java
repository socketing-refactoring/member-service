package com.jeein.member.controller;

import com.jeein.member.dto.common.CommonResponseDTO;
import com.jeein.member.dto.request.JoinRequestDTO;
import com.jeein.member.dto.request.LoginRequestDTO;
import com.jeein.member.dto.request.UpdateNicknameRequestDTO;
import com.jeein.member.dto.request.UpdatePasswordRequestDTO;
import com.jeein.member.dto.response.*;
import com.jeein.member.service.MemberService;
import jakarta.validation.Valid;

import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    // 모든 회원 목록 조회
    @GetMapping
    public ResponseEntity<CommonResponseDTO<List<GetMemberResponseDTO>>> getAllMembers() {
        return ResponseEntity.ok(memberService.getAllMembers());
    }

    // 특정 회원 조회
    @GetMapping("/{id}")
    public ResponseEntity<CommonResponseDTO<GetMemberResponseDTO>> getMember(
            @PathVariable String id) {
        return ResponseEntity.ok(memberService.getMemberById(id));
    }

    // 닉네임 업데이트
    @PatchMapping("/{id}/nickname")
    public ResponseEntity<CommonResponseDTO<UpdateNicknameResponseDTO>> updateNickname(
            @PathVariable String id, @Valid @RequestBody UpdateNicknameRequestDTO updateNicknameRequest) {
        return ResponseEntity.ok(memberService.updateNickname(id, updateNicknameRequest));
    }

    // 비밀번호 업데이트
    @PatchMapping("/{id}/password")
    public ResponseEntity<CommonResponseDTO<Void>> updatePassword(
            @PathVariable String id, @Valid @RequestBody UpdatePasswordRequestDTO updatePasswordRequest) {
        return ResponseEntity.ok(memberService.updatePassword(id, updatePasswordRequest));
    }

    // 회원 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<CommonResponseDTO<Void>> deleteMember(@PathVariable String id) {
        return ResponseEntity.ok(memberService.deleteMember(id));
    }

    /*----------------------------------
     * MemberFeignClient
    ----------------------------------*/
    // 새로운 회원 등록
    @PostMapping("/join")
    public ResponseEntity<CommonResponseDTO<JoinResponseDTO>> joinMember(
            @Valid @RequestBody JoinRequestDTO joinRequestDTO) {
        CommonResponseDTO<JoinResponseDTO> response = memberService.joinMember(joinRequestDTO);

        return ResponseEntity.status(HttpStatus.CREATED)
                .location(URI.create("api/v1/members/" + response.getData().getId()))
                .body(response);
    }

    // 회원 로그인 인증
    @PostMapping("/login")
    public ResponseEntity<CommonResponseDTO<LoginResponseDTO>> loginRequestDTO(
            @Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        return ResponseEntity.ok(memberService.loginMember(loginRequestDTO));
    }
}
