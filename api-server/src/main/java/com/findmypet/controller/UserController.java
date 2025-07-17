package com.findmypet.controller;

import com.findmypet.domain.user.User;
import com.findmypet.dto.request.ChangePasswordRequest;
import com.findmypet.dto.request.LoginRequest;
import com.findmypet.dto.request.RegisterRequest;
import com.findmypet.dto.request.UpdateUserInfoRequest;
import com.findmypet.dto.response.UserResponse;
import com.findmypet.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.findmypet.config.auth.SessionConst.SESSION_USER_ID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody RegisterRequest request) {
        User user = userService.register(request);
        return ResponseEntity.ok(UserResponse.from(user));
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@RequestBody LoginRequest request, HttpSession session) {
        User user = userService.login(request);
        session.setAttribute(SESSION_USER_ID, user.getId());

        log.info("[회원 로그인] 세션 ID: {}, 사용자 ID: {}", session.getId(), user.getId());

        return ResponseEntity.ok(UserResponse.from(user));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute(SESSION_USER_ID);
        HttpSession session = request.getSession(false);

        if (session != null) {
            log.info("[회원 로그아웃] 세션 ID: {}, 사용자 ID: {}", session.getId(), userId);
            session.invalidate();
        }

        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateInfo(
            @PathVariable Long id,
            @RequestBody UpdateUserInfoRequest request) {
        userService.updateInfo(id, request);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<Void> changePassword(@PathVariable Long id, @RequestBody ChangePasswordRequest request) {
        userService.changePassword(id, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        userService.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}
