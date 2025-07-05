package com.findmypet.dto.request;

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

