package com.markmycode.mmc.user.repository;

import com.markmycode.mmc.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    // 존재하는 이메일인지 확인
    boolean existsByUserEmail(String userEmail);
    // 존재하는 닉네임인지 확인
    boolean existsByUserNickname(String userNickname);
    // 사용자 이메일을 기반으로 사용자 정보 조회
    User findByUserEmail(String userEmail);
}
