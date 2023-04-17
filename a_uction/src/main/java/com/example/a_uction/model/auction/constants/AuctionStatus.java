package com.example.a_uction.model.auction.constants;

public enum AuctionStatus {
    PROCEEDING ("진행중"),
    SCHEDULED ("예정된"),
    COMPLETED ("완료된");

    String condition;

    AuctionStatus(String condition) {
        this.condition = condition;
    }
}
