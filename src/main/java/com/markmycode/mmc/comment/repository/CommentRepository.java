package com.markmycode.mmc.comment.repository;

import com.markmycode.mmc.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByPostPostIdAndParentCommentIsNullOrderByCommentCreatedAt(Long postId);
    List<Comment> findByParentCommentCommentIdOrderByCommentCreatedAt(Long parentId);

}
