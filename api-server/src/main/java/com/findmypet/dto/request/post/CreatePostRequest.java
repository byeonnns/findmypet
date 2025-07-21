package com.findmypet.dto.request.post;

import com.findmypet.domain.common.PetType;
import com.findmypet.domain.post.PostType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreatePostRequest {
    private Long writerId;
    private PostType postType;
    private String title;
    private String location;
    private String description;
    private PetType petSpecies;
    private String petBreed;
    private int petAge;
    private String petGender;
    private String petColor;
}