package com.example.a_uction.model.file.repository;

import com.example.a_uction.model.file.entity.FileEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends JpaRepository<FileEntity, Long> {
	void deleteAllByAuction_AuctionId(Long auctionId);
	Optional<List<FileEntity>> findAllByAuction_AuctionId(Long auctionId);

}
