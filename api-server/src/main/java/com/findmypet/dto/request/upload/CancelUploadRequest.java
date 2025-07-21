package com.findmypet.dto.request.upload;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CancelUploadRequest {
    private String uploadId;
}
