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
public class PostPreviewResponseDto {

    private Long postId;
    private String postTitle;
    private LocalDateTime postCreatedAt;
    private long postLikeCount;
    private String userNickname;
//    private long postCommentCount;

}
