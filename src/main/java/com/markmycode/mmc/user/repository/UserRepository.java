package com.markmycode.mmc.user.repository;

import com.markmycode.mmc.user.entity.User;
import com.markmycode.mmc.user.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    // 존재하는 이메일인지 확인
    boolean existsByUserEmailAndUserStatus(String userEmail, Status userStatus);
    // 활동 회원 중 존재하는 닉네임인지 확인
    boolean existsByUserNicknameAndUserStatus(String userNickname, Status userStatus);
    // 사용자 이메일을 기반으로 사용자 정보 조회
    User findByUserEmail(String userEmail);
    // 소셜 아이디로 사용자 정보 조회
    User findBySocialId(String socialUserId);
}
