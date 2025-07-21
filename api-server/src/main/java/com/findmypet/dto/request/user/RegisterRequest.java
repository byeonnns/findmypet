package com.findmypet.dto.request.user;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    private String loginId;
    private String password;
    private String name;
    private String phone;
}

