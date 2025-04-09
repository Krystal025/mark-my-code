package com.markmycode.mmc.post.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter // 클라이언트에서 필요
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostResponseDto {

    private Long postId;
    private Long userId;
    private Integer parentCategoryId;
    private Integer childCategoryId;
    private Integer platformId;
    private Integer languageId;
    private String postTitle;
    private String postContent;
    private String problemLink;
    private LocalDateTime postCreatedAt;
    private LocalDateTime postUpdatedAt;
    private long postCommentCount;
    private long postLikeCount;
    private String userNickname;
    private String parentCategoryName;
    private String childCategoryName;
    private String platformName;
    private String languageName;

}
