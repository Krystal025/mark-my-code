package com.markmycode.mmc.user.entity;

import com.markmycode.mmc.user.enums.Provider;
import com.markmycode.mmc.user.enums.Role;
import com.markmycode.mmc.user.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true) // 기존 인스턴스를 복사하여 새로운 인스턴스를 만들고, 일부 필드만 변경할 수 있도록 함
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // AUTO_INCREMENT
    private Long userId;

    @Column(nullable = false)
    private String userName;

    @Column(nullable = false, unique = true)
    private String userEmail;

    @Column
    private String userPwd;

    @Column(nullable = false, unique = true)
    private String userNickname;

    @Enumerated(EnumType.STRING)
    private Status userStatus;

    @Column(updatable = false)
    private LocalDateTime userCreatedAt;

    @Enumerated(EnumType.STRING)
    private Role userRole;

    @Column(length = 100, unique = true)
    private String socialId;

    @Enumerated(EnumType.STRING)
    private Provider socialProvider;

    @PrePersist // 처음 생성되는 엔티티가 DB에 저장되기 전에 호출
    private void onCreate(){
        if(this.userStatus == null){
            this.userStatus = Status.ACTIVE;
        }
        if(this.userRole == null){
            this.userRole = Role.ROLE_USER;
        }
        this.userCreatedAt = LocalDateTime.now();
    }

}
