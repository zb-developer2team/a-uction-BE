package com.example.a_uction.model.auctionTransactionHistory.dto;

import com.example.a_uction.model.auctionTransactionHistory.entity.AuctionTransactionHistoryEntity;
import lombok.Builder;

public class AuctionTransactionHistoryDto {

	@Builder
	static class Response {

		private String sellerEmail;
		private String itemName;
		private int price;
		private String buyerEmail;
		private String imageSrc;

		public static AuctionTransactionHistoryDto.Response fromEntity(
			AuctionTransactionHistoryEntity auctionTransactionHistory) {
			return Response.builder()
				.sellerEmail(auctionTransactionHistory.getSellerEmail())
				.buyerEmail(auctionTransactionHistory.getBuyerEmail())
				.itemName(auctionTransactionHistory.getItemName())
				.price(auctionTransactionHistory.getPrice())
				.imageSrc(auctionTransactionHistory.getImageSrc())
				.build();
		}
	}
}
