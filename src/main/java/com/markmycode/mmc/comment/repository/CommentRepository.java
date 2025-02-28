package com.markmycode.mmc.comment.repository;

import com.markmycode.mmc.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

}
