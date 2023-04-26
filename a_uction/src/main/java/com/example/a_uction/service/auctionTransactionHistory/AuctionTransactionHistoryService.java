package com.example.a_uction.service.auctionTransactionHistory;

import com.example.a_uction.model.auctionTransactionHistory.dto.AuctionTransactionHistoryDto;
import com.example.a_uction.model.auctionTransactionHistory.entity.AuctionTransactionHistoryEntity;
import com.example.a_uction.model.auctionTransactionHistory.repository.AuctionTransactionHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuctionTransactionHistoryService {

    private final AuctionTransactionHistoryRepository auctionTransactionHistoryRepository;


    public List<AuctionTransactionHistoryDto.Response> getAllTransactionHistory(String userEmail) {
        List<AuctionTransactionHistoryEntity> entities = auctionTransactionHistoryRepository.getAllByBuyerEmail(userEmail);
        ArrayList<AuctionTransactionHistoryDto.Response> transactionList = new ArrayList<>();
        for (AuctionTransactionHistoryEntity entity : entities){
            transactionList.add(AuctionTransactionHistoryDto.Response.fromEntity(entity));
        }
        return transactionList;
    }
}
