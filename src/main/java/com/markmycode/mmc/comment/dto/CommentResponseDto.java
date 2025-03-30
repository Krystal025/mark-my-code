package com.markmycode.mmc.comment.dto;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    // 부모 객체에서 자식 객체의 리스트를 직렬화하여 순환참조를 방지함 (직렬화 : 객체 -> JSON 형식으로 변환)
    @JsonManagedReference
    private List<CommentResponseDto> childComments = new ArrayList<>(); // null 대신 빈 컬렉션([])을 반환
}
