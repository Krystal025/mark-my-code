package com.markmycode.mmc.post.entity;

import com.markmycode.mmc.category.entity.Category;
import com.markmycode.mmc.language.entity.Language;
import com.markmycode.mmc.platform.entity.Platform;
import com.markmycode.mmc.post.dto.PostRequestDto;
import com.markmycode.mmc.post.dto.PostPreviewResponseDto;
import com.markmycode.mmc.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor // 기본 생성자 (JPA에서 객체 생성시 필요)
@AllArgsConstructor // 빌더에서 사용하는 필드를 받는 생성자가 필요, final 또는 @NotNull이 아닌 필드를 포함하기 위해 @RequiredArgsConstructor 대신 사용
@Builder(toBuilder = true) // 빌더 패턴으로 객체 생성, toBuilder=true로 빌더 객체에서 기존 객체를 수정 가능
// @DynamicUpdate  // 변경된 필드만 업데이트
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // AUTO_INCREMENT
    private Long postId;

    // FetchType.LAZY : 해당 정보가 필요할 때 로딩하도록 설정
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;  // User 엔티티와의 관계

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "platform_id", nullable = false)
    private Platform platform;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "language_id", nullable = false)
    private Language language;

    @Column(nullable = false, length = 255)
    private String postTitle;

    @Column(nullable = false, columnDefinition = "MEDIUMTEXT")
    private String postContent;

    @Column
    private String problemLink;

    @Column(updatable = false)
    private LocalDateTime postCreatedAt;

    @Column
    private LocalDateTime postUpdatedAt;

    @Column(nullable = false)
    private long postLikeCount;

    @PrePersist // 처음 생성되는 엔티티가 DB에 저장되기 전에 호출됨
    private void onCreate(){
        this.postCreatedAt = LocalDateTime.now();
        this.postUpdatedAt = LocalDateTime.now();
        this.postLikeCount = 0;
    }

    @PreUpdate // 이미 존재하는 엔티티가 수정되어 DB에 반영되기 전에 호출됨
    private void onUpdate(){
        this.postUpdatedAt = LocalDateTime.now();
    }

    public static Post fromDto(PostRequestDto dto, User user, Category category, Platform platform, Language language) {
        return Post.builder()
                .user(user)
                .category(category)
                .platform(platform)
                .language(language)
                .postTitle(dto.getPostTitle())
                .postContent(dto.getPostContent())
                .problemLink(dto.getProblemLink())
                .build();
    }

    public PostPreviewResponseDto toPreviewDto() {
        return PostPreviewResponseDto.builder()
                .postId(postId)
                .postTitle(postTitle)
                .postCreatedAt(postCreatedAt)
                .postLikeCount(postLikeCount)
                .userNickname(user.getUserNickname())
                .build();
    }

    // 게시글 정보 변경을 위한 도메인 메서드 (Setter 직접 노출 방지)
    public void updateCategory(Category category) {
        this.category = category;
    }

    public void updatePlatform(Platform platform) {
        this.platform = platform;
    }

    public void updateLanguage(Language language) {
        this.language = language;
    }

    public void updateTitle(String title) {
        this.postTitle = title;
    }

    public void updateContent(String content) {
        this.postContent = content;
    }

    public void updateLink(String link) {this.problemLink = link; }

    public void incrementLikeCount(){
        this.postLikeCount += 1;
    }

    public void decrementLikeCount(){
        this.postLikeCount -= 1;
    }

}
