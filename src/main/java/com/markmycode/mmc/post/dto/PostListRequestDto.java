package com.markmycode.mmc.post.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PostListRequestDto {

    private Integer parentCategoryId;
    private Integer childCategoryId;
    private Integer platformId;
    private Integer languageId;
    private int page = 1; // 클라이언트가 요청한 페이지 번호 (기본값 : 1)
    private int size = 12; // 한 페이지에 보여줄 데이터 개수 (기본값 : 12)
    // private String sort;
    // private String direction;

    public int getOffset(){
        if(page < 1) page = 1; // 페이지 번호 최소값 보장
        if(size > 100) size = 100; // 페이지 크기 최대값 제한 (성능 보호)
        return (page - 1) * size;
    }

}
