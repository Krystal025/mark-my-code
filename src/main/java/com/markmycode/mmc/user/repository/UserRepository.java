package com.markmycode.mmc.user.repository;

import com.markmycode.mmc.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUserEmail(String userEmail);
    boolean existsByUserNickname(String userNickname);

}
