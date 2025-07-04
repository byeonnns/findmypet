package com.findmypet.domain.common;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Embeddable
public class Pet {

    @Enumerated(EnumType.STRING)
    private PetType species;

    private String breed; // 품종

    private int age;

    private String gender;

    private String color;
}
