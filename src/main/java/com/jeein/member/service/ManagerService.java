package com.jeein.member.service;

import static com.jeein.member.ResponseMessage.JOIN_SUCCESS;
import static com.jeein.member.ResponseMessage.LOGIN_SUCCESS;

import com.jeein.member.dto.common.CommonResponseDTO;
import com.jeein.member.dto.request.JoinRequestDTO;
import com.jeein.member.dto.request.LoginRequestDTO;
import com.jeein.member.dto.request.UpdateNicknameRequestDTO;
import com.jeein.member.dto.request.UpdatePasswordRequestDTO;
import com.jeein.member.dto.response.GetManagerResponseDTO;
import com.jeein.member.dto.response.ManagerJoinResponseDTO;
import com.jeein.member.dto.response.ManagerLoginResponseDTO;
import com.jeein.member.dto.response.UpdateManagerNicknameResponseDTO;
import com.jeein.member.entity.Manager;
import com.jeein.member.entity.ManagerDetail;
import com.jeein.member.exception.AuthException;
import com.jeein.member.exception.ErrorCode;
import com.jeein.member.exception.ManagerException;
import com.jeein.member.repository.ManagerRepository;
import java.time.Instant;
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
public class ManagerService {

    private final ManagerRepository managerRepository;

    // 매니저 등록
    @Transactional
    public CommonResponseDTO<ManagerJoinResponseDTO> joinManager(JoinRequestDTO joinRequestDTO) {
        // Manager 엔티티 생성
        Manager manager =
                Manager.builder()
                        .name(joinRequestDTO.getName())
                        .nickname(joinRequestDTO.getNickname())
                        .email(joinRequestDTO.getEmail())
                        .build();

        // ManagerDetail 엔티티 생성 (비밀번호에 Bcrypt(salt + hash) 암호화 적용)
        ManagerDetail managerDetail =
                ManagerDetail.builder()
                        .password(BCrypt.hashpw(joinRequestDTO.getPassword(), BCrypt.gensalt()))
                        .manager(manager)
                        .build();

        manager.addManagerDetail(managerDetail);

        // 이메일 중복 예외 처리
        managerRepository
                .findByEmail(manager.getEmail())
                .ifPresent(
                        existingManager -> {
                            throw new AuthException(ErrorCode.ALREADY_EXISTING_EMAIL);
                        });

        // 닉네임 중복 예외 처리
        managerRepository
                .findByNickname(manager.getNickname())
                .ifPresent(
                        existingManager -> {
                            throw new AuthException(ErrorCode.ALREADY_EXISTING_NICKNAME);
                        });

        // Manager, ManagerDetail 저장
        Manager savedManager = managerRepository.save(manager);

        log.debug("joined manager: {}", savedManager);

        return CommonResponseDTO.success(
                JOIN_SUCCESS, "0", ManagerJoinResponseDTO.fromEntity(savedManager));
    }

    // 매니저 로그인 인증
    @Transactional
    public CommonResponseDTO<ManagerLoginResponseDTO> loginManager(
            LoginRequestDTO loginRequestDTO) {
        // 이메일 검증
        Manager manager =
                managerRepository
                        .findManagerWithDetailsByEmail(loginRequestDTO.getEmail())
                        .orElseThrow(() -> new AuthException(ErrorCode.INVALID_MEMBER));

        // 비밀번호 검증
        if (!BCrypt.checkpw(
                loginRequestDTO.getPassword(), manager.getManagerDetail().getPassword())) {
            throw new AuthException(ErrorCode.INVALID_PASSWORD);
        }

        log.debug("login manager: {}", manager);
        return CommonResponseDTO.success(
                LOGIN_SUCCESS, "0", ManagerLoginResponseDTO.fromEntity(manager));
    }

    // 모든 매니저 조회
    public CommonResponseDTO<List<GetManagerResponseDTO>> getAllManagers() {
        List<Manager> managers = managerRepository.findAll();

        return CommonResponseDTO.success(
                "매니저 목록 조회가 성공적으로 이루어졌습니다.",
                "0",
                managers.stream()
                        .map(GetManagerResponseDTO::fromEntity)
                        .collect(Collectors.toList()));
    }

    // 특정 매니저 조회
    public CommonResponseDTO<GetManagerResponseDTO> getManagerById(String id) {
        Manager manager =
                managerRepository
                        .findById(UUID.fromString(id))
                        .orElseThrow(() -> new ManagerException(ErrorCode.MEMBER_NOT_FOUND));

        log.debug("selected manager: {}", manager);
        return CommonResponseDTO.success(
                "매니저 조회가 성공적으로 이루어졌습니다.", "0", GetManagerResponseDTO.fromEntity(manager));
    }

    // 닉네임 업데이트
    @Transactional
    public CommonResponseDTO<UpdateManagerNicknameResponseDTO> updateNickname(
            String id, UpdateNicknameRequestDTO updateNicknameRequest) {
        // 매니저 조회
        Manager manager =
                managerRepository
                        .findById(UUID.fromString(id))
                        .orElseThrow(() -> new ManagerException(ErrorCode.MEMBER_NOT_FOUND));
        log.debug("selected manager: {}", manager);

        // 동일한 닉네임인지 검사
        String newNickname = updateNicknameRequest.getNickname();
        if (manager.getNickname().equals(newNickname)) {
            throw new ManagerException(ErrorCode.UNCHANGED_NICKNAME);
        }

        // 닉네임 업데이트
        manager.updateNickname(newNickname);
        Manager savedManager = managerRepository.save(manager);
        log.debug("saved manager: {}", savedManager);

        return CommonResponseDTO.success(
                "닉네임 변경이 성공적으로 이루어졌습니다.",
                "0",
                UpdateManagerNicknameResponseDTO.fromEntity(savedManager));
    }

    // 비밀번호 업데이트
    @Transactional
    public CommonResponseDTO<Void> updatePassword(
            String id, UpdatePasswordRequestDTO updatePasswordRequest) {
        // 매니저 조회
        Manager manager =
                managerRepository
                        .findById(UUID.fromString(id))
                        .orElseThrow(() -> new ManagerException(ErrorCode.MEMBER_NOT_FOUND));
        log.debug("selected manager: {}", manager);

        // 동일한 비밀번호인지 검사
        String newPassword = updatePasswordRequest.getPassword();
        if (manager.getManagerDetail().getPassword().equals(newPassword)) {
            throw new ManagerException(ErrorCode.UNCHANGED_PASSWORD);
        }

        // 비밀번호 업데이트
        manager.getManagerDetail().updatePassword(newPassword);
        managerRepository.save(manager); // not necessary

        return CommonResponseDTO.success("비밀번호 변경이 성공적으로 이루어졌습니다.", "0", null);
    }

    // 매니저 삭제
    @Transactional
    public CommonResponseDTO<Void> deleteManager(String id) {
        Manager manager =
                managerRepository
                        .findById(UUID.fromString(id))
                        .orElseThrow(() -> new ManagerException(ErrorCode.MEMBER_NOT_FOUND));
        log.debug("selected manager: {}", manager);

        managerRepository.softDelete(manager.getId(), Instant.now());

        return CommonResponseDTO.success("매니저 탈퇴가 성공적으로 이루어졌습니다.", "0", null);
    }
}
