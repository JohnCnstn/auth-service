package com.johncnstn.auth.repository;

import com.johncnstn.auth.entity.UserEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    UserEntity findByEmail(String email);
}
