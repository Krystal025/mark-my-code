package com.markmycode.mmc.comment.service;

import com.markmycode.mmc.comment.dto.CommentRequestDto;
import com.markmycode.mmc.comment.entity.Comment;
import com.markmycode.mmc.comment.repository.CommentRepository;
import com.markmycode.mmc.exception.ErrorCode;
import com.markmycode.mmc.exception.custom.BadRequestException;
import com.markmycode.mmc.exception.custom.NotFoundException;
import com.markmycode.mmc.post.entity.Post;
import com.markmycode.mmc.post.service.PostService;
import com.markmycode.mmc.user.entity.User;
import com.markmycode.mmc.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    private final UserService userService;
    private final PostService postService;

    public void createComment(Long userId, Long postId, CommentRequestDto requestDto){
        // 사용자 확인
        User user = userService.getUser(userId);
        // 게시글 확인
        Post post = postService.getPost(postId);
        // 댓글 유효성 검사
        validateComment(requestDto.getParentCommentId(), postId);
        // 댓글 엔티티 생성
        Comment comment = Comment.builder()
                .user(user)
                .post(post)
                .parentComment(getParentComment(requestDto.getParentCommentId()))
                .commentContent(requestDto.getCommentContent())
                .build();
        // 댓글 저장
        commentRepository.save(comment);
    }

    // 유효성 검사
    public void validateComment(Long parentCommentId, Long postId){
        // 부모 댓글 확인 (부모 댓글 ID가 전달된 경우)
        if(parentCommentId != null){
            Comment parentComment = commentRepository.findById(parentCommentId)
                    .orElseThrow(()-> new NotFoundException(ErrorCode.COMMENT_NOT_FOUND));
            // 부모 댓글이 해당 게시글에 속하는지 확인
            if(!parentComment.getPost().getPostId().equals(postId)){
                throw new BadRequestException(ErrorCode.INVALID_PARENT_COMMENT);
            }
        }
    }

    // 해당 ID에 대한 댓글 엔티티 객체 반환
    public Comment getComment(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.COMMENT_NOT_FOUND));
    }

    // 해당 ID에 대한 (부모)댓글 엔티티 객체 반환
    public Comment getParentComment(Long parentCommentId){
        if (parentCommentId == null){
            return null;
        }
        return commentRepository.findById(parentCommentId)
                .orElseThrow(()-> new NotFoundException(ErrorCode.COMMENT_NOT_FOUND));
    }

}
