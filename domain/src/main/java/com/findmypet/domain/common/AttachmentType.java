package com.findmypet.domain.common;

public enum AttachmentType {
    POST,
    MESSAGE;

    public String getFolder() {
        return switch (this) {
            case POST -> "posts";
            case MESSAGE -> "messages";
        };
    }
}
