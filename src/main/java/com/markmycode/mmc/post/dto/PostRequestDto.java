package com.markmycode.mmc.post.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostRequestDto {

    private Integer childCategoryId;
    private Integer platformId;
    private Integer languageId;
    private String postTitle;
    private String postContent;

}
