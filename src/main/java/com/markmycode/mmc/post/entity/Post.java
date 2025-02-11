package com.markmycode.mmc.post.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// DB 테이블과 매핑되어 게시글 정보를 저장하는 Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post {

    private Long postId;
    private Long userId;
    private Integer platformId;
    private Integer categoryId;
    private Integer languageId;
    private String postTitle;
    private String postContent;
    private LocalDateTime postCreatedAt;
    private LocalDateTime postUpdatedAt;
    private Integer postLike;

}
