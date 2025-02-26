package com.markmycode.mmc.like.repository;

import com.markmycode.mmc.like.entity.PostLike;
import com.markmycode.mmc.post.entity.Post;
import com.markmycode.mmc.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    // 좋아요 여부 확인
    boolean existsByUserAndPost(User user, Post post);
    // 좋아요 조회
    PostLike findByUserAndPost(User user, Post post);
    // 좋아요 취소
    void deleteByUserAndPost(User user, Post post);

}
