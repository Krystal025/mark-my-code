package com.markmycode.mmc.post.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagedPostResponseDto {

    private List<PostPreviewResponseDto> posts; // 게시글 목록
    private int currentPage; // 현재 페이지
    private int pageSize; // 페이지 크기
    private long totalPosts; // 전체 게시글 개수
    private int totalPages; // 전체 페이지 수

}
