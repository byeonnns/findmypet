package com.findmypet.domain.message;

public enum MessageThreadStatus {
    PENDING("답변대기중"),
    ANSWERED("답변완료");

    private final String displayName;

    MessageThreadStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
