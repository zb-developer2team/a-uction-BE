package com.example.a_uction.model.user.entity;

import com.example.a_uction.model.auction.entity.AuctionEntity;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import com.example.a_uction.model.user.dto.ModifyUser;
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
	private List<AuctionEntity> auctions = new ArrayList<>();

	@Column(columnDefinition = "int default 0")
	private Integer balance;

	@CreatedDate
	private LocalDateTime createDateTime;
	@LastModifiedDate
	private LocalDateTime updateDateTime;

	private String description;

	public void updateUserEntity(ModifyUser.Request updateRequest){
		this.setUsername(updateRequest.getUsername());
		this.setPhoneNumber(updateRequest.getPhoneNumber());
		this.setDescription(updateRequest.getDescription());
	}

}
