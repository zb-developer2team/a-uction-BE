package com.example.a_uction.model.auction.constants;

public enum TransactionStatus {
    NOT_SALE ("판매할 수 없음"),
    SALE ("판매할 수 있음");

    String message;

    TransactionStatus(String message) {
        this.message = message;
    }
}
