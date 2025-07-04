package com.findmypet.domain.post;

public enum PostStatus {

    UNRESOLVED("미해결"),
    MATCHED("매칭됨"),
    RESOLVED("해결");

    private final String displayName;

    PostStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

}

