package com.markmycode.mmc.like.service;

import com.markmycode.mmc.like.entity.PostLike;
import com.markmycode.mmc.like.repository.PostLikeMapper;
import com.markmycode.mmc.like.repository.PostLikeRepository;
import com.markmycode.mmc.post.dto.PostPreviewResponseDto;
import com.markmycode.mmc.post.entity.Post;
import com.markmycode.mmc.post.service.PostService;
import com.markmycode.mmc.user.dto.UserSummaryDto;
import com.markmycode.mmc.user.entity.User;
import com.markmycode.mmc.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostLikeService {

    private final PostLikeMapper postLikeMapper;

    private final PostLikeRepository postLikeRepository;

    private final UserService userService;
    private final PostService postService;

    // 좋아요 토글
    @Transactional
    public void toggleLikePost(Long userId, Long postId){
        User user = userService.getUser(userId);
        Post post = postService.getPost(postId);
        // 이미 좋아요가 존재하는 경우
        if (postLikeRepository.existsByUserAndPost(user, post)){
            // 좋아요 수 감소
            post.decrementLikeCount();
            // 좋아요 객체 삭제
            postLikeRepository.deleteByUserAndPost(user, post);
        } else{
            // 좋아요 수 증가
            post.incrementLikeCount();
            // 좋아요 객체 생성 및 저장
            PostLike postLike = PostLike.create(user, post);
            postLikeRepository.save(postLike);
        }
    }

    public boolean isLikedByUser(User user, Post post) {
        return postLikeRepository.existsByUserAndPost(user, post);
    }

    // 사용자가 좋아요를 누른 게시글 목록 조회 (-> PostService에서 처리)
    public List<PostPreviewResponseDto> getLikedPosts(Long userId){
        return postLikeMapper.selectLikedPostsByUser(userId);
    }

    // 특정 게시글에 대한 좋아요를 누른 사용자 목록 조회
    public List<UserSummaryDto> getUsersWhoLiked(Long postId){
        return postLikeMapper.selectUsersWhoLikedPost(postId);
    }

}
