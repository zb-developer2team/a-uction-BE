package com.example.a_uction.model.user.repository;

import com.example.a_uction.model.user.entity.UserVerificationEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserVerificationEntityRepository extends JpaRepository<UserVerificationEntity, Long> {
	Optional<UserVerificationEntity> findByCode(String code);
}
