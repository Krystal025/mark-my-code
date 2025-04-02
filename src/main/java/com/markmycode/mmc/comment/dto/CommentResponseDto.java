package com.markmycode.mmc.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponseDto {

    private Long commentId;
    private Long postId;
    private Long userId;
    private Long parentId;
    private String commentContent;
    private String commentStatus;
    private LocalDateTime commentCreatedAt;
    private LocalDateTime commentUpdatedAt;
    private String userNickname;
    private String userStatus;
    //private int childCount;

}
