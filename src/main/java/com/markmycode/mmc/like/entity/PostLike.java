package com.markmycode.mmc.like.entity;

import com.markmycode.mmc.post.entity.Post;
import com.markmycode.mmc.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "post_like")
public class PostLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long likeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column
    private LocalDateTime likeCreatedAt = LocalDateTime.now();

    public PostLike(User user, Post post){
        this.user = user;
        this.post = post;
    }

    // PostLike 객체 생성 (정적 팩토리 메소드)
    public static PostLike create(User user, Post post){
        return new PostLike(user, post);
    }

}
