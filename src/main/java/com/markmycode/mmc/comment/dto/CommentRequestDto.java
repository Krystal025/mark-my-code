package com.markmycode.mmc.comment.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentRequestDto {

    private Long parentCommentId;
    private String commentContent;

}
