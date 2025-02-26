package com.markmycode.mmc.post.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

// 클라이언트와 데이터를 주고받기 위해 사용되는 데이터 전송 객체
@Getter
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
