package com.example.a_uction.security.jwt;


import com.example.a_uction.security.jwt.dto.TokenDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Setter
@Slf4j
@Component
public class JwtProvider {
	private static final String TOKEN_HEADER = "Authorization";
	private static final String REFRESH_TOKEN_HEADER = "Authorization-refresh";
	private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
	private static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";
	private static final String CLAIM_KEY = "userEmail";
	private static final String ROLE = "USER";

	@Value("${jwt.prefix}")
	private String tokenPrefix;
	@Value("${jwt.access.expiration}")
	private long accessTokenExpireTime;
	@Value("${jwt.refresh.expiration}")
	private long refreshTokenExpireTime;
	@Value("${spring.jwt.secret}")
	protected String secretKey;

	public TokenDto createToken(String userEmail) {
		return new TokenDto(this.createAccessToken(userEmail), this.createRefreshToken(),
			refreshTokenExpireTime);
	}

	public String createAccessToken(String userEmail) {
		Claims claims = Jwts.claims();
		claims.put(CLAIM_KEY, userEmail);

		long time = System.currentTimeMillis();

		return Jwts.builder()
			.setClaims(claims)
			.setSubject(ACCESS_TOKEN_SUBJECT)
			.setIssuedAt(new Date(time))
			.setExpiration(new Date(time + accessTokenExpireTime))
			.signWith(SignatureAlgorithm.HS256, secretKey)
			.compact();
	}

	public String createRefreshToken() {
		long time = System.currentTimeMillis();

		return Jwts.builder()
			.setSubject(REFRESH_TOKEN_SUBJECT)
			.setIssuedAt(new Date())
			.setExpiration(new Date(time + refreshTokenExpireTime))
			.signWith(SignatureAlgorithm.HS256, secretKey)
			.compact();
	}

	public String getUserEmail(String token) {
		return parseClaims(token).get(CLAIM_KEY, String.class);
	}

	public long getExpiration(String token) {
		long time = System.currentTimeMillis();
		Date expiration = parseClaims(token).getExpiration();
		return expiration.getTime() - time;
	}

	public boolean validateToken(String token) {
		try {
			Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
			return true;
		} catch (IllegalArgumentException e) {
			log.info("토큰은 필수입니다.", e);
		} catch (MalformedJwtException e) {
			log.info("손상된 토큰입니다.", e);
		} catch (ExpiredJwtException e) {
			log.info("만료된 토큰입니다.", e);
		} catch (UnsupportedJwtException e) {
			log.info("지원하지 않는 토큰입니다.", e);
		} catch (SignatureException e) {
			log.info("시그니처 검증에 실패한 토큰입니다.", e);
		}
		return false;
	}

	public UsernamePasswordAuthenticationToken getAuthentication(String token) {
		return new UsernamePasswordAuthenticationToken(
			getUserEmail(token), null, List.of(new SimpleGrantedAuthority(ROLE))
		);
	}

	private Claims parseClaims(String token) {
		try {
			return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
		} catch (ExpiredJwtException e) {
			return e.getClaims();
		}
	}

	private String resolveTokenFromRequest(HttpServletRequest request, String header) {

		String token = request.getHeader(header);

		if (!ObjectUtils.isEmpty(token) && token.startsWith(tokenPrefix)) {
			return token.substring(tokenPrefix.length());
		}
		return null;
	}

	public String resolveAccessTokenFromRequest(HttpServletRequest request) {
		return this.resolveTokenFromRequest(request, TOKEN_HEADER);
	}

	public String resolveRefreshTokenFromRequest(HttpServletRequest request) {
		return this.resolveTokenFromRequest(request, REFRESH_TOKEN_HEADER);
	}

}
