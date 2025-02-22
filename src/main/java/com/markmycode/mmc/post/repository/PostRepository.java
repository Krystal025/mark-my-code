package com.markmycode.mmc.post.repository;

import com.markmycode.mmc.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {

}
