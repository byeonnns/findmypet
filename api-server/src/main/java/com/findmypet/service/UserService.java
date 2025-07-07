package com.findmypet.service;
import com.findmypet.common.exception.PermissionDeniedException;
import com.findmypet.common.exception.ResourceNotFoundException;
import com.findmypet.dto.request.LoginRequest;
import com.findmypet.dto.request.RegisterRequest;
import com.findmypet.domain.user.User;
import com.findmypet.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User register(RegisterRequest request) {
        if (userRepository.existsByLoginId(request.getLoginId())) {
            log.info("[회원가입 실패] 중복 loginId = {}", request.getLoginId());
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }

        User user = User.register(
                request.getLoginId(),
                request.getPassword(),
                request.getName(),
                request.getPhone()
        );

        log.info("[회원가입] loginId = {}, userId = {}", user.getLoginId(), user.getId());

        return userRepository.save(user);
    }

    @Transactional
    public User login(LoginRequest request) {
        User user = userRepository.findByLoginId(request.getLoginId())
                .orElseThrow(() -> {
                    log.warn("[로그인 실패] 존재하지 않는 loginId = {}", request.getLoginId());
                    return new ResourceNotFoundException("존재하지 않는 사용자입니다.");
                });

        try {
            user.login(request.getPassword());
        } catch (IllegalArgumentException e) {
            log.warn("[로그인 실패] 잘못된 비밀번호 입력 loginId = {}", user.getLoginId());
            throw new PermissionDeniedException("잘못된 비밀번호입니다.");
        }

        log.info("[회원 로그인] loginId = {}, userId = {}", user.getLoginId(), user.getId());

        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("[회원 조회 실패] 존재하지 않는 userId = {}", id);
                    return new ResourceNotFoundException("사용자를 찾을 수 없습니다.");
                });
    }
}