package com.example.a_uction.model.auctionSearch.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SortProperties {

    ID ("id", "경매 id"),
    START_DATE_TIME ("startDateTime", "시작일"),
    END_DATE_TIME ("endDateTime", "종료일"),
    STARTING_PRICE ("startingPrice", "시작금액")

    ;

    private final String property;
    private final String description;
}
