package com.findmypet.domain.user;

import com.findmypet.domain.common.BaseTimeEntity;
import com.findmypet.util.EncryptUtils;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
@Table(name = "users")
@Entity
public class User extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
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

    public static User register(String loginId, String password, String name, String phone) {
        return User.builder()
                .loginId(loginId)
                .password(EncryptUtils.hash(password))
                .name(name)
                .phone(phone)
                .isActive(true)
                .build();
    }

    public void login(String password) {
        if (!EncryptUtils.verify(password, this.password)) {
            throw new IllegalArgumentException("비밀번호가 올바르지 않습니다.");
        }
        updateLastLoginTime();
    }

    public void changePassword(String newPassword) {
        this.password = EncryptUtils.hash(newPassword);
    }

    public void deactivate() {
        this.isActive = false;
    }

    public void updateLastLoginTime() {
        this.lastLoginTime = LocalDateTime.now();
    }

    public void updateUserInfo(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }
}
