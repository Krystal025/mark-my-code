package com.markmycode.mmc.post.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter // 클라이언트에서 필요
@Builder
@AllArgsConstructor
public class PostResponseDto {

    private Long postId;
    private String postTitle;
    private String postContent;
    private LocalDateTime postCreatedAt;
    private LocalDateTime postUpdatedAt;
    private long postLikeCount;
    private String userNickname;
    private String childCategoryName;
    private String parentCategoryName;
    private String platformName;
    private String languageName;

}
