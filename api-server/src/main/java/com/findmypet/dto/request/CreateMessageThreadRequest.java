package com.findmypet.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateMessageThreadRequest {
    private Long postId;
    private String content;
}
