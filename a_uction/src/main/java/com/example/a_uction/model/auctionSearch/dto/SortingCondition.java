package com.example.a_uction.model.auctionSearch.dto;

import com.example.a_uction.model.auctionSearch.constants.SortProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Sort;

@Getter
@Setter
public class SortingCondition {
    private SortProperties sortProperties;
    private Sort.Direction direction;

}
