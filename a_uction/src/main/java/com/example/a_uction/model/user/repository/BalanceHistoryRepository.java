package com.example.a_uction.model.user.repository;

import com.example.a_uction.model.user.entity.BalanceHistoryEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BalanceHistoryRepository extends JpaRepository<BalanceHistoryEntity, Long> {

	Optional<BalanceHistoryEntity> findFirstByUserIdOrderByIdDesc(Long id);

}
