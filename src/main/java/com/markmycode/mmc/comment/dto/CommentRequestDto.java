package com.markmycode.mmc.comment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CommentRequestDto {

    private Long parentId;

    @NotBlank(message = "댓글 내용을 입력해주세요.")
    private String commentContent;

}
