package com.findmypet.domain.post;

public enum PostType {

    LOSTED("분실"),
    FOUNDED("발견");

    private final String displayName;

    PostType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
