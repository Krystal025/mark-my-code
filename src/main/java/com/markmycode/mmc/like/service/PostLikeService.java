package com.markmycode.mmc.like.service;

import com.markmycode.mmc.exception.ErrorCode;
import com.markmycode.mmc.exception.custom.DuplicateException;
import com.markmycode.mmc.exception.custom.ForbiddenException;
import com.markmycode.mmc.exception.custom.NotFoundException;
import com.markmycode.mmc.like.entity.PostLike;
import com.markmycode.mmc.like.repository.PostLikeRepository;
import com.markmycode.mmc.post.entity.Post;
import com.markmycode.mmc.post.service.PostService;
import com.markmycode.mmc.user.entity.User;
import com.markmycode.mmc.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostLikeService {

    private final PostLikeRepository postLikeRepository;

    private final UserService userService;
    private final PostService postService;

    // 좋아요 등록
    @Transactional
    public void likePost(Long userId, Long postId){
        User user = userService.getUser(userId);
        Post post = postService.getPost(postId);
        if (post.getUser().getUserId().equals(userId)) {
            throw new ForbiddenException(ErrorCode.CANNOT_LIKE_OWN_POST);
        }
        // 이미 좋아요가 존재하는 경우
        if (postLikeRepository.existsByUserAndPost(user, post)) {
            throw new DuplicateException(ErrorCode.LIKE_ALREADY_EXIST);
        }
        // 좋아요 수 증가
        post.incrementLikeCount();
        // 좋아요 객체 생성 및 저장
        PostLike postLike = PostLike.create(user, post);
        postLikeRepository.save(postLike);
    }

    // 좋아요 취소
    @Transactional
    public void unlikePost(Long userId, Long postId){
        User user = userService.getUser(userId);
        Post post = postService.getPost(postId);
        // 해당 사용자의 좋아요 객체 조회
        PostLike postLike = postLikeRepository.findByUserAndPost(user, post);
        // 좋아요가 존재하지 않는 경우
        if (postLike == null) {
            throw new NotFoundException(ErrorCode.LIKE_NOT_FOUND);
        }
        // 좋아요 누른 사용자와 취소 요청하는 사용자가 일치하는지 확인
        if (!postLike.getUser().getUserId().equals(userId)) {
            throw new ForbiddenException(ErrorCode.CANNOT_UNLIKE);
        }
        // 좋아요 수 감소
        post.decrementLikeCount();
        // 좋아요 객체 삭제
        postLikeRepository.deleteByUserAndPost(user, post);
    }

    // 사용자가 좋아요를 누른 게시글 목록 조회 (-> PostService에서 처리)
    // 사용자가 특정 게시글에 좋아요를 눌렀는지 확인
    // 특정 게시글에 대한 좋아요를 누른 사용자 목록 조회
}
