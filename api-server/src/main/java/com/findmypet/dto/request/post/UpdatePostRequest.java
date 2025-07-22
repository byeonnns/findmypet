package com.findmypet.dto.request.post;

import com.findmypet.domain.common.PetType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class UpdatePostRequest {
    private String postType;
    private String title;
    private String location;
    private String description;

    // 펫 정보
    private PetType petSpecies;
    private String petBreed;
    private int petAge;
    private String petGender;
    private String petColor;
}