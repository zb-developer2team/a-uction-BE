package com.example.a_uction.model.auction.constants;

public enum Category {
    CLOTHES ("의류"),
    SHOES("신발"),
    BAG("가방"),
    ARTWORK("예술품"),
    ETC("기타")
    ;

    String type;

    Category(String type) {
        this.type = type;
    }
}
