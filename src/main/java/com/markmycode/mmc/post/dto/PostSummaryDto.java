package com.markmycode.mmc.post.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostSummaryDto {

    private Long postId;
    private Integer platformId;
    private Integer categoryId;
    private Integer languageId;
    private String postTitle;
    private LocalDateTime postCreatedAt;
    private Integer postLike;
    private String userNickname;

}
