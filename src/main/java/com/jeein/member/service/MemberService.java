package com.jeein.member.service;

import com.jeein.member.dto.common.CommonResponseDTO;
import com.jeein.member.dto.request.JoinRequestDTO;
import com.jeein.member.dto.request.LoginRequestDTO;
import com.jeein.member.dto.response.GetMemberResponseDTO;
import com.jeein.member.dto.response.JoinResponseDTO;
import com.jeein.member.dto.response.LoginResponseDTO;
import com.jeein.member.dto.response.UpdateNicknameResponseDTO;
import com.jeein.member.entity.Member;
import com.jeein.member.entity.MemberDetail;
import com.jeein.member.exception.ErrorCode;
import com.jeein.member.exception.MemberException;
import com.jeein.member.exception.UnknownException;
import com.jeein.member.repository.MemberRepository;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;

    // 회원 등록
    @Transactional
    public CommonResponseDTO<JoinResponseDTO> joinMember(JoinRequestDTO joinRequestDTO)
            throws UnknownException {
        // Member 엔티티 생성
        Member member =
                Member.builder()
                        .name(joinRequestDTO.getName())
                        .nickname(joinRequestDTO.getNickname())
                        .email(joinRequestDTO.getEmail())
                        .build();

        // MemberDetail 엔티티 생성 (비밀번호에 Bcrypt(salt + hash) 암호화 적용)
        MemberDetail memberDetail =
                MemberDetail.builder()
                        .password(BCrypt.hashpw(joinRequestDTO.getPassword(), BCrypt.gensalt()))
                        .member(member)
                        .build();

        member.addMemberDetail(memberDetail);

        // 이메일 중복 예외 처리
        memberRepository
                .findByEmail(member.getEmail())
                .ifPresent(
                        existingMember -> {
                            throw new MemberException(ErrorCode.EMAIL_ALREADY_EXISTS);
                        });

        // 닉네임 중복 예외 처리
        memberRepository
                .findByNickname(member.getNickname())
                .ifPresent(
                        existingMember -> {
                            throw new MemberException(ErrorCode.NICKNAME_ALREADY_EXISTS);
                        });

        // Member, MemberDetail 저장
        Member savedMember = memberRepository.save(member);

        log.debug("joined member: {}", savedMember);

        return CommonResponseDTO.success(
                "회원가입이 성공적으로 이루어졌습니다.", "0", JoinResponseDTO.fromEntity(savedMember));
    }

    // 회원 로그인 인증
    @Transactional
    public CommonResponseDTO<LoginResponseDTO> loginMember(LoginRequestDTO loginRequestDTO)
            throws UnknownException {
        // 이메일 검증
        Member member =
                memberRepository
                        .findMemberWithDetailsByEmail(loginRequestDTO.getEmail())
                        .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

        // 비밀번호 검증
        if (!BCrypt.checkpw(
                loginRequestDTO.getPassword(), member.getMemberDetail().getPassword())) {
            throw new MemberException(ErrorCode.PASSWORD_FAILED);
        }

        log.debug("login member: {}", member);
        return CommonResponseDTO.success(
                "로그인이 성공적으로 이루어졌습니다.", "0", LoginResponseDTO.fromEntity(member));
    }

    // 모든 회원 조회
    public CommonResponseDTO<List<GetMemberResponseDTO>> getAllMembers() {
        List<Member> members = memberRepository.findAll();

        return CommonResponseDTO.success(
                "회원 목록 조회가 성공적으로 이루어졌습니다.",
                "0",
                members.stream()
                        .map(GetMemberResponseDTO::fromEntity)
                        .collect(Collectors.toList()));
    }

    // 특정 회원 조회
    public CommonResponseDTO<GetMemberResponseDTO> getMemberById(String id)
            throws UnknownException {
        Member member =
                memberRepository
                        .findById(UUID.fromString(id))
                        .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

        log.debug("selected member: {}", member);
        return CommonResponseDTO.success(
                "회원 조회가 성공적으로 이루어졌습니다.", "0", GetMemberResponseDTO.fromEntity(member));
    }

    // 닉네임 업데이트
    @Transactional
    public CommonResponseDTO<UpdateNicknameResponseDTO> updateNickname(
            String id, String newNickname) throws UnknownException {
        // 회원 조회
        Member member =
                memberRepository
                        .findById(UUID.fromString(id))
                        .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
        log.debug("selected member: {}", member);

        // 동일한 닉네임인지 검사
        if (member.getNickname().equals(newNickname)) {
            throw new MemberException(ErrorCode.UNCHANGED_NICKNAME);
        }

        // 닉네임 업데이트
        member.updateNickname(newNickname);
        memberRepository.save(member);

        return CommonResponseDTO.success(
                "닉네임 변경이 성공적으로 이루어졌습니다.", "0", UpdateNicknameResponseDTO.fromEntity(member));
    }

    // 비밀번호 업데이트
    @Transactional
    public CommonResponseDTO<Void> updatePassword(String id, String newPassword)
            throws UnknownException {
        // 회원 조회
        Member member =
                memberRepository
                        .findById(UUID.fromString(id))
                        .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
        log.debug("selected member: {}", member);

        // 동일한 비밀번호인지 검사
        if (member.getMemberDetail().getPassword().equals(newPassword)) {
            throw new MemberException(ErrorCode.UNCHANGED_PASSWORD);
        }

        // 비밀번호 업데이트
        member.getMemberDetail().updatePassword(newPassword);
        memberRepository.save(member); // not necessary

        return CommonResponseDTO.success("비밀번호 변경이 성공적으로 이루어졌습니다.", "0", null);
    }

    // 회원 삭제
    @Transactional
    public CommonResponseDTO<Void> deleteMember(String id) throws UnknownException {
        Member member =
                memberRepository
                        .findById(UUID.fromString(id))
                        .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
        log.debug("selected member: {}", member);

        memberRepository.softDelete(member.getId());

        return CommonResponseDTO.success("회원 탈퇴가 성공적으로 이루어졌습니다.", "0", null);
    }
}
