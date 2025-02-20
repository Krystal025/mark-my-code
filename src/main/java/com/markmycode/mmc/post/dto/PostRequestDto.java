package com.markmycode.mmc.post.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostRequestDto {

    private Long userId;
    private Integer childCategoryId;
    private Integer platformId;
    private Integer languageId;
    private String postTitle;
    private String postContent;

}
