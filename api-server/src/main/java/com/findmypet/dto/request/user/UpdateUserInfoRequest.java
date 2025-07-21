package com.findmypet.dto.request.user;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateUserInfoRequest {
    private String name;
    private String phone;
}
