package com.findmypet.service;

import com.findmypet.common.exception.user.DuplicateUserException;
import com.findmypet.common.exception.user.UserNotFoundException;
import com.findmypet.common.exception.user.InvalidCredentialsException;
import com.findmypet.domain.user.User;
import com.findmypet.dto.request.user.ChangePasswordRequest;
import com.findmypet.dto.request.user.LoginRequest;
import com.findmypet.dto.request.user.RegisterRequest;
import com.findmypet.dto.request.user.UpdateUserInfoRequest;
import com.findmypet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public User register(RegisterRequest request) {
        if (userRepository.existsByLoginId(request.getLoginId())) {
            log.warn("[회원가입 실패] 중복 loginId={}", request.getLoginId());
            throw new DuplicateUserException(request.getLoginId());
        }

        User user = User.register(
                request.getLoginId(),
                request.getPassword(),
                request.getName(),
                request.getPhone()
        );

        log.info("[회원가입] loginId={}, userId={}", user.getLoginId(), user.getId());
        return userRepository.save(user);
    }

    @Transactional
    public User login(LoginRequest request) {
        User user = userRepository.findByLoginId(request.getLoginId())
                .orElseThrow(() -> {
                    log.warn("[로그인 실패] 존재하지 않는 loginId={}", request.getLoginId());
                    return new UserNotFoundException(request.getLoginId());
                });

        try {
            user.login(request.getPassword());
        } catch (IllegalArgumentException e) {
            log.warn("[로그인 실패] 잘못된 비밀번호 입력 loginId={}", request.getLoginId());
            throw new InvalidCredentialsException(request.getLoginId());
        }

        log.info("[회원 로그인] loginId={}, userId={}", user.getLoginId(), user.getId());
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("[회원 조회 실패] 존재하지 않는 userId={}", id);
                    return new UserNotFoundException(id);
                });
    }

    @Transactional
    public void updateInfo(Long id, UpdateUserInfoRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("[회원 정보 수정 실패] 존재하지 않는 userId={}", id);
                    return new UserNotFoundException(id);
                });

        user.updateUserInfo(request.getName(), request.getPhone());

        log.info("[회원 정보 수정] userId={}, name={}, phone={}", id, request.getName(), request.getPhone());
    }

    @Transactional
    public void changePassword(Long id, ChangePasswordRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("[비밀번호 변경 실패] 존재하지 않는 userId={}", id);
                    return new UserNotFoundException(id);
                });

        user.changePassword(request.getNewPassword());

        log.info("[비밀번호 변경] userId={}", id);
    }

    @Transactional
    public void deactivate(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("[회원 탈퇴 실패] 존재하지 않는 userId={}", id);
                    return new UserNotFoundException(id);
                });

        user.deactivate();

        log.info("[회원 탈퇴] userId={}", id);
    }
}
