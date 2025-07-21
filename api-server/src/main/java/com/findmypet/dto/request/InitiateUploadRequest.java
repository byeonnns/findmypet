package com.findmypet.dto.request;

import com.findmypet.domain.common.AttachmentType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class InitiateUploadRequest {
    private AttachmentType attachmentType;
    private Long targetId;
    private List<FileRequest> files;

    @Getter @NoArgsConstructor @AllArgsConstructor
    public static class FileRequest {
        private String filename;
        private String contentType;
        private long size;
        String finalUrl;
    }
}
