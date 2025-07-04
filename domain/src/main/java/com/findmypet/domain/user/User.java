package com.findmypet.domain.user;

import com.findmypet.util.PasswordUtils;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
@Table(name = "users")
@Entity
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String loginId;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private boolean isActive;

    private LocalDateTime lastLoginTime;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public static User register(String loginId, String password, String name, String phone) {
        return User.builder()
                .loginId(loginId)
                .password(PasswordUtils.hash(password))
                .name(name)
                .phone(phone)
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public void login(String password) {
        if (!PasswordUtils.verify(password, this.password)) {
            throw new IllegalArgumentException("비밀번호가 올바르지 않습니다.");
        }
        updateLastLoginTime();
    }

    public void changePassword(String newPassword) {
        this.password = PasswordUtils.hash(newPassword);
        stampUpdated();
    }

    public void deactivate() {
        this.isActive = false;
        stampUpdated();
    }

    public void updateLastLoginTime() {
        this.lastLoginTime = LocalDateTime.now();
    }

    public void updateUserInfo(String name, String phone) {
        this.name = name;
        this.phone = phone;
        stampUpdated();
    }

    private void stampUpdated() {
        this.updatedAt = LocalDateTime.now();
    }
}
