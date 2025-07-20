package com.findmypet.dto.request;

import com.findmypet.domain.common.AttachmentType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class InitiateUploadRequest {
    private String filename;
    private String contentType;
    private long size;
    private AttachmentType attachmentType;
    private Long targetId;
}