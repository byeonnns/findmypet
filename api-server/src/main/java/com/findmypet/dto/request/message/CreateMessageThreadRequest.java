package com.findmypet.dto.request.message;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateMessageThreadRequest {
    private Long postId;
    private String content;
}
