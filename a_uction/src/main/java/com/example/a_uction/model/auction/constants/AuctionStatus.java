package com.example.a_uction.model.auction.constants;

public enum AuctionStatus {
    PROCEEDING ("경매 진행중"),
    COMPLETE ("경매 진행 완료"),
    FAILED ("경매 실패");

    String message;

    AuctionStatus(String message) {
        this.message = message;
    }
}
