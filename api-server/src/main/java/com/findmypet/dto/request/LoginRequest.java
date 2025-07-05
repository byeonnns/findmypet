package com.findmypet.dto.request;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    private String loginId;
    private String password;
}
