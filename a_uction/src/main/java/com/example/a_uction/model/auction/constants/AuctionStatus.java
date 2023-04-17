package com.example.a_uction.model.auction.constants;

public enum AuctionStatus {
    PROCEEDING ("진행중"),
    SCHEDULED ("예정된"),
    COMPLETED ("완료된");

    final String condition;

    AuctionStatus(String condition) {
        this.condition = condition;
    }
}
