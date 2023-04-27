package com.example.a_uction.service.wish;

import com.example.a_uction.exception.AuctionException;
import com.example.a_uction.model.auction.dto.AuctionDto;
import com.example.a_uction.model.auction.entity.AuctionEntity;
import com.example.a_uction.model.auction.repository.AuctionRepository;
import com.example.a_uction.model.user.entity.UserEntity;
import com.example.a_uction.model.user.repository.UserRepository;
import com.example.a_uction.model.wishList.dto.WishListResponse;
import com.example.a_uction.model.wishList.entity.WishListEntity;
import com.example.a_uction.model.wishList.repository.WishRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.a_uction.exception.constants.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class WishService {

    private final WishRepository wishRepository;
    private final UserRepository userRepository;
    private final AuctionRepository auctionRepository;

    private UserEntity getUser(String userEmail) {
        return userRepository.getByUserEmail(userEmail);
    }

    private AuctionEntity getAuction(Long auctionId) {
        return auctionRepository.getByAuctionId(auctionId);
    }

    private Optional<WishListEntity> existWishList(Long auctionId, Long userId){
        return wishRepository.findByWishUserIdAndWishAuctionAuctionId(userId, auctionId);
    }

    public WishListResponse addWishAuction(Long auctionId, String userEmail){
        UserEntity user = getUser(userEmail);
        Optional<WishListEntity> wishCheck = existWishList(auctionId, user.getId());

        if(wishCheck.isPresent()){
            throw new AuctionException(ALREADY_EXIST_WISH_ITEM);
        }

        return new WishListResponse().fromEntity(wishRepository.save(
                WishListEntity.builder()
                        .wishAuction(getAuction(auctionId))
                        .wishUser(user)
                        .build())
        );
    }

    public Page<AuctionDto.Response> getUserWishList(String userEmail, Pageable pageable) {
        UserEntity user = getUser(userEmail);

        if(user.getWishList().size() == 0){
            throw new AuctionException(WISH_LIST_IS_EMPTY);
        }

        List<AuctionDto.Response> wishList = user.getWishList().stream()
                .map((WishListEntity m) -> AuctionDto.Response.fromEntity(m.getWishAuction()))
                .collect(Collectors.toList());

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), wishList.size());

        return new PageImpl<>(wishList.subList(start, end), pageable, wishList.size());
    }

    public Page<String> getWishUserListAboutAuction(Long auctionId, String userEmail, Pageable pageable){
        AuctionEntity result = auctionRepository.getByAuctionId(auctionId);
        if(result.getUser().getUserEmail().equals(userEmail)){
            List<String> userList = result.getWishListForUser().stream()
                    .map(m -> m.getWishUser().getUserEmail())
                    .collect(Collectors.toList());

            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), userList.size());

            return new PageImpl<>(userList.subList(start, end), pageable, userList.size());
        }
        return new PageImpl<>(List.of(String.valueOf(result.getWishListForUser().size())));
    }

    public WishListResponse deleteWishAuction(Long auctionId, String userEmail) {
        UserEntity user = getUser(userEmail);
        WishListEntity wishCheck = existWishList(auctionId, user.getId())
                .orElseThrow(() -> new AuctionException(UNABLE_DELETE_WISH_LIST));
        wishRepository.delete(wishCheck);
        return new WishListResponse().fromEntity(wishCheck);
    }
}
