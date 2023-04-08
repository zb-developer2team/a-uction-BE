package com.example.a_uction.security.jwt;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JwtProvider {

	private static final long TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24;
	private static final String CLAIM_KEY = "userEmail";
	private static final String ROLE = "USER";
	@Value("${spring.jwt.secret}")
	private String secretKey;

	public String createToken(String userEmail) {
		Claims claims = Jwts.claims();
		claims.put(CLAIM_KEY, userEmail);

		long time = System.currentTimeMillis();

		return Jwts.builder()
			.setClaims(claims)
			.setIssuedAt(new Date(time))
			.setExpiration(new Date(time + TOKEN_EXPIRE_TIME))
			.signWith(SignatureAlgorithm.HS256, secretKey)
			.compact();
	}

	public String getUserEmail(String token) {
		return parseClaims(token).get(CLAIM_KEY, String.class);
	}

	public boolean validateToken(String token){

		try {
			Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
			return true;
		} catch (IllegalArgumentException e) {
			log.error("토큰은 필수입니다.", e);
		} catch (MalformedJwtException e) {
			log.error("손상된 토큰입니다.", e);
		} catch(ExpiredJwtException e) {
			log.error("만료된 토큰입니다.", e);
		} catch (UnsupportedJwtException e) {
			log.error("지원하지 않는 토큰입니다.", e);
		} catch (SignatureException e) {
			log.error("시그니처 검증에 실패한 토큰입니다.", e);
		}

		return false;
	}

	public UsernamePasswordAuthenticationToken getAuthentication(String token) {
		return new UsernamePasswordAuthenticationToken(
			getUserEmail(token), null, List.of(new SimpleGrantedAuthority(ROLE))
		);
	}

	private Claims parseClaims(String token) {

		return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
	}
}
