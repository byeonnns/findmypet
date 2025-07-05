package com.findmypet.dto.request;

import com.findmypet.domain.common.PetType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@NoArgsConstructor
public class UpdatePostRequest {
    private String title;
    private String location;
    private String description;
    private List<String> attachmentUrls;

    private PetType petSpecies;
    private String petBreed;
    private Integer petAge;
    private String petGender;
    private String petColor;
}