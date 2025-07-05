package com.findmypet.dto.response;

import com.findmypet.domain.common.Attachment;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AttachmentResponse {
    private Long id;
    private String url;
    private int sortOrder;

    public static AttachmentResponse from(Attachment a) {
        return new AttachmentResponse(a.getId(), a.getUrl(), a.getSortOrder());
    }
}