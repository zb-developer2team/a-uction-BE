package com.example.a_uction.model.user.entity;

import com.example.a_uction.model.auction.entity.AuctionEntity;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import com.example.a_uction.model.user.dto.ModifyUser;
import com.example.a_uction.model.wishList.entity.WishListEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class UserEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Email
	@NotNull
	private String userEmail;
	private String password;
	private String username;
	private String phoneNumber;
	private String profileImageSrc;

	@OneToMany(mappedBy = "user")
	@Builder.Default
	private List<AuctionEntity> auctions = new ArrayList<>();

	@Column(columnDefinition = "int default 0")
	private Integer balance;

	@CreatedDate
	private LocalDateTime createDateTime;
	@LastModifiedDate
	private LocalDateTime updateDateTime;

	private String description;

	@OneToMany(mappedBy = "wishUser")
	private List<WishListEntity> wishList = new ArrayList<>();

	public void updateUserEntity(ModifyUser.Request updateRequest){
		this.setUsername(updateRequest.getUsername());
		this.setPhoneNumber(updateRequest.getPhoneNumber());
		this.setDescription(updateRequest.getDescription());
	}

}
