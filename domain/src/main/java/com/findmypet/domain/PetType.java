package com.findmypet.domain;

public enum PetType {
    DOG("개"),
    CAT("고양이"),
    OTHER("기타");

    private final String displayName;

    PetType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
