package com.findmypet.dto.response;

import com.findmypet.domain.user.User;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    private Long id;
    private String loginId;
    private String name;
    private String phone;
    private boolean active;
    private LocalDateTime lastLoginTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static UserResponse from(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .loginId(user.getLoginId())
                .name(user.getName())
                .phone(user.getPhone())
                .active(user.isActive())
                .lastLoginTime(user.getLastLoginTime())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
