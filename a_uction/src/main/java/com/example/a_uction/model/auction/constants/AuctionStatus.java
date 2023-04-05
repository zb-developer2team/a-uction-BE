package com.example.a_uction.model.auction.constants;

public enum AuctionStatus {
    SCHEDULED ("경매 진행 예정"),
    PROCEEDING ("경매 진행중"),
    COMPLETE ("경매 진행 완료"),
    FAILED ("경매 실패");

    private final String message;

    AuctionStatus(String message) {
        this.message = message;
    }
}
