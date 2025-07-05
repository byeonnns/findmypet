package com.findmypet.controller;

import com.findmypet.domain.user.User;
import com.findmypet.dto.request.LoginRequest;
import com.findmypet.dto.request.RegisterRequest;
import com.findmypet.dto.response.UserResponse;
import com.findmypet.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private static final String SESSION_USER_ID = "USER_ID";

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody RegisterRequest request) {
        User user = userService.register(request);
        return ResponseEntity.ok(UserResponse.from(user));
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@RequestBody LoginRequest request, HttpSession session
    ) {
        User user = userService.login(request);
        session.setAttribute(SESSION_USER_ID, user.getId());
        return ResponseEntity.ok(UserResponse.from(user));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(HttpSession session) {
        Long userId = (Long) session.getAttribute(SESSION_USER_ID);
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        User user = userService.findById(userId);
        return ResponseEntity.ok(UserResponse.from(user));
    }
}

