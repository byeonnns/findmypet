package com.findmypet.domain;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Embeddable
public class MyLostPet {

    @Enumerated(EnumType.STRING)
    private PetType petType;   // 종(개, 고양이, 기타 등)

    private String breed;     // 품종

    private String color;     // 색상

    private Integer age;      // 나이(대략)

    private String gender;    // 성별
}
