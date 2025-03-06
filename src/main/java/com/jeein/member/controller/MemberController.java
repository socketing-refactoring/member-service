package com.jeein.member.controller;

import com.jeein.member.dto.common.CommonResponseDTO;
import com.jeein.member.dto.request.JoinRequestDTO;
import com.jeein.member.dto.request.LoginRequestDTO;
import com.jeein.member.dto.response.*;
import com.jeein.member.service.MemberService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
    @PutMapping("/{id}/nickname")
    public ResponseEntity<CommonResponseDTO<UpdateNicknameResponseDTO>> updateNickname(
            @PathVariable String id, @Valid @RequestBody String nickname) {
        return ResponseEntity.ok(memberService.updateNickname(id, nickname));
    }

    // 비밀번호 업데이트
    @PutMapping("/{id}/password")
    public ResponseEntity<CommonResponseDTO<Void>> updatePassword(
            @PathVariable String id, @Valid @RequestBody String password) {
        return ResponseEntity.ok(memberService.updatePassword(id, password));
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
    public CommonResponseDTO<JoinResponseDTO> joinMember(
            @Valid @RequestBody JoinRequestDTO joinRequestDTO) {
        return memberService.joinMember(joinRequestDTO);
    }

    // 회원 로그인 인증
    @PostMapping("/login")
    public CommonResponseDTO<LoginResponseDTO> loginRequestDTO(
            @Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        return memberService.loginMember(loginRequestDTO);
    }
}
