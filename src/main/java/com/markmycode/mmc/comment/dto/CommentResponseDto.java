package com.markmycode.mmc.comment.dto;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter // 클라이언트에서 필요
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponseDto {

    private Long commentId;
    private Long postId;
    private Long parentId;
    private String commentContent;
    private LocalDateTime commentCreatedAt;
    private LocalDateTime commentUpdatedAt;
    private String userNickname;
    // 부모 객체에서 자식 객체의 리스트를 직렬화하여 순환참조를 방지함 (직렬화 : 객체 -> JSON 형식으로 변환)
    @JsonManagedReference
    private List<CommentResponseDto> childComments;

}
