package com.example.a_uction.model.auction.constants;

public enum TransactionStatus {
    FAILED ("거래 실패"),
    COMPLETE ("거래 성공");

    String message;

    TransactionStatus(String message) {
        this.message = message;
    }
}
