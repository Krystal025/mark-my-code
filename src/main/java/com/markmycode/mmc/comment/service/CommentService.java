package com.markmycode.mmc.comment.service;

import com.markmycode.mmc.comment.dto.CommentRequestDto;
import com.markmycode.mmc.comment.entity.Comment;
import com.markmycode.mmc.comment.repository.CommentRepository;
import com.markmycode.mmc.exception.ErrorCode;
import com.markmycode.mmc.exception.custom.BadRequestException;
import com.markmycode.mmc.exception.custom.ForbiddenException;
import com.markmycode.mmc.exception.custom.NotFoundException;
import com.markmycode.mmc.post.entity.Post;
import com.markmycode.mmc.post.service.PostService;
import com.markmycode.mmc.user.entity.User;
import com.markmycode.mmc.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        Comment parentComment = validateAndGetParentComment(requestDto.getParentCommentId(), postId);
        // 댓글 엔티티 생성
        Comment comment = Comment.builder()
                .user(user)
                .post(post)
                .parentComment(parentComment)
                .commentContent(requestDto.getCommentContent())
                .build();
        // 댓글 저장
        commentRepository.save(comment);
    }

    @Transactional
    public void updateComment(Long userId, Long commentId, CommentRequestDto requestDto){
        User user = userService.getUser(userId);
        Comment comment = getComment(commentId);
        // 댓글 작성자와 요청한 사용자 간 일치 여부 확인
        validateCommentOwnership(user, comment);
        // 변경된 필드 반영
        if (requestDto.getCommentContent() != null){
            comment.updateContent(requestDto.getCommentContent());
        }
    }

    @Transactional
    public void deactivateComment(Long userId, Long commentId){
        User user = userService.getUser(userId);
        Comment comment = getComment(commentId);
        // 관리자일 경우 비활성화 처리 가능
        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
        if (isAdmin){
            comment.deactivate();
            return;
        }
        validateCommentOwnership(user, comment);
        // 본인 댓글 비활성화(삭제)
        comment.deactivate();
    }

    // 유효성 검사 및 부모 댓글 엔티티 객체 반환
    public Comment validateAndGetParentComment(Long parentCommentId, Long postId){
        if (parentCommentId == null){
            return null; // 불필요한 DB 조회 방지
        }
        // 부모 댓글 확인 (부모 댓글 ID가 전달된 경우)
        Comment parentComment = commentRepository.findById(parentCommentId)
                .orElseThrow(()-> new NotFoundException(ErrorCode.PARENT_COMMENT_NOT_FOUND));
        // 부모 댓글이 해당 게시글에 속하는지 확인
        if(!parentComment.getPost().getPostId().equals(postId)){
            throw new BadRequestException(ErrorCode.INVALID_COMMENT);
        }
        return parentComment;
    }

    // 게시글 작성자 유효성 검사
    private void validateCommentOwnership(User user, Comment comment){
        // JPA 엔티티는 equals()를 재정의하여 식별자 비교로 소유자 확인시 효율성을 높임
        if(!comment.getUser().equals(user)){
            throw new ForbiddenException(ErrorCode.USER_NOT_MATCH);
        }
    }

    // 해당 ID에 대한 댓글 엔티티 객체 반환
    public Comment getComment(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.COMMENT_NOT_FOUND));
    }

}
