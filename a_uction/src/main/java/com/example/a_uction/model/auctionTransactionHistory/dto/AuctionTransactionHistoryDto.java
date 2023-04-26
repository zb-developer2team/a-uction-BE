package com.example.a_uction.model.auctionTransactionHistory.dto;

import com.example.a_uction.model.auctionTransactionHistory.entity.AuctionTransactionHistoryEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class AuctionTransactionHistoryDto {

	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Response {

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
