package com.example.a_uction.model.auction.constants;

public enum ItemStatus {
    GOOD ("좋음"),
    BEST ("최상"),
    BAD ("나쁨"),

    ;

    final String condition;

    ItemStatus(String condition) {
        this.condition = condition;
    }
}
