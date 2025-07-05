package com.findmypet.dto.request;

import com.findmypet.domain.common.PetType;
import com.findmypet.domain.post.PostType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@NoArgsConstructor
public class CreatePostRequest {
    private Long writerId;
    private PostType postType;
    private String title;
    private String location;
    private String description;
    private List<String> attachmentUrls;
    private PetType petSpecies;
    private String petBreed;
    private int petAge;
    private String petGender;
    private String petColor;
}