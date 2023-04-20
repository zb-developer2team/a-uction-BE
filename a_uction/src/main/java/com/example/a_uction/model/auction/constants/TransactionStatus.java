package com.example.a_uction.model.auction.constants;

public enum TransactionStatus {
    TRANSACTION_COMPLETE ("거래 성공"),
    TRANSACTION_FAIL ("거래 실패");

    final String message;

    TransactionStatus(String message) {
        this.message = message;
    }
}
