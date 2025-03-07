package com.markmycode.mmc.post.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PostRequestDto {

    private Integer childCategoryId;
    private Integer platformId;
    private Integer languageId;
    private String postTitle;
    private String postContent;

}
