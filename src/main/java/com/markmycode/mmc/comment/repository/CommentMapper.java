package com.markmycode.mmc.comment.repository;

import com.markmycode.mmc.comment.dto.CommentResponseDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {

    List<CommentResponseDto> selectCommentsByPostId(Long postId);
    List<CommentResponseDto> selectChildCommentsByParentId(Long parentId); // MyBatis 내부적으로 사용중

}
