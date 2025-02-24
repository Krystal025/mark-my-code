package com.markmycode.mmc.post.entity;

import com.markmycode.mmc.category.entity.Category;
import com.markmycode.mmc.language.entity.Language;
import com.markmycode.mmc.platform.entity.Platform;
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
// @DynamicUpdate  // 변경된 필드만 업데이트
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // AUTO_INCREMENT
    private Long postId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;  // User 엔티티와의 관계

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne
    @JoinColumn(name = "platform_id", nullable = false)
    private Platform platform;

    @ManyToOne
    @JoinColumn(name = "language_id", nullable = false)
    private Language language;

    @Column(nullable = false, length = 255)
    private String postTitle;

    @Column(nullable = false, columnDefinition = "MEDIUMTEXT")
    private String postContent;

    @Column(updatable = false)
    private LocalDateTime postCreatedAt;

    @Column
    private LocalDateTime postUpdatedAt;

    @Column(nullable = false)
    private Integer postLike;

    @PrePersist // 처음 생성되는 엔티티가 DB에 저장되기 전에 호출됨
    private void onCreate(){
        this.postCreatedAt = LocalDateTime.now();
        this.postUpdatedAt = LocalDateTime.now();
        this.postLike = 0;
    }

    @PreUpdate // 이미 존재하는 엔티티가 수정되어 DB에 반영되기 전에 호출됨
    private void onUpdate(){
        this.postUpdatedAt = LocalDateTime.now();
    }

    // 게시글 정보 변경을 위한 도메인 메서드 (Setter 직접 노출 방지)
    public void changeCategory(Category category) {
        this.category = category;
    }

    public void changePlatform(Platform platform) {
        this.platform = platform;
    }

    public void changeLanguage(Language language) {
        this.language = language;
    }

    public void updateTitle(String title) {
        if (title != null) {
            this.postTitle = title;
        }
    }

    public void updateContent(String content) {
        if (content != null) {
            this.postContent = content;
        }
    }
}
