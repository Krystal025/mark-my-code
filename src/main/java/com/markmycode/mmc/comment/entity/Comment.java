package com.markmycode.mmc.comment.entity;

import com.markmycode.mmc.post.entity.Post;
import com.markmycode.mmc.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "post_comment")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parentComment;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String commentContent;

    @Column(updatable = false)
    private LocalDateTime commentCreatedAt;

    @Column
    private LocalDateTime commentUpdatedAt;

    @PrePersist
    private void onCreate(){
        this.commentCreatedAt = LocalDateTime.now();
        this.commentUpdatedAt = LocalDateTime.now();
    }

    @PreUpdate
    private void onUpdate(){
        this.commentUpdatedAt = LocalDateTime.now();
    }

}
