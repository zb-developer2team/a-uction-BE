package com.example.a_uction.security.jwt;

import static com.example.a_uction.exception.constants.ErrorCode.EMPTY_TOKEN_ERROR;
import static com.example.a_uction.exception.constants.ErrorCode.EXPIRED_TOKEN;
import static com.example.a_uction.exception.constants.ErrorCode.FAILED_VERIFY_SIGNATURE;
import static com.example.a_uction.exception.constants.ErrorCode.INVALID_TOKEN;
import static com.example.a_uction.exception.constants.ErrorCode.LOGOUT_USER_ERROR;

import com.example.a_uction.exception.constants.ErrorCode;
import com.example.a_uction.exception.dto.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.SignatureException;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtProvider provider;
	private final RedisTemplate<String, Object> redisTemplate;
	private final ObjectMapper objectMapper;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		String path = request.getServletPath();
		log.info("REQUEST [ SERVLET_PATH : {} ]", path);
		if (isPass(path)) {
			filterChain.doFilter(request, response);
			return;
		}

		String token = provider.resolveAccessTokenFromRequest(request);

		try {
			provider.validateToken(token);
			if (!this.isBlocked(token)) {
				UsernamePasswordAuthenticationToken authentication =
					provider.getAuthentication(token);

				authentication.setDetails(
					new WebAuthenticationDetailsSource().buildDetails(request));

				SecurityContextHolder.getContext()
					.setAuthentication(authentication);

				filterChain.doFilter(request, response);
			} else {
				setResponse(response, LOGOUT_USER_ERROR);
			}
		} catch (IllegalArgumentException e) {
			log.info("토큰은 필수입니다.", e);
			setResponse(response, EMPTY_TOKEN_ERROR);
		} catch (ExpiredJwtException e) {
			log.info("만료된 토큰입니다.", e);
			setResponse(response, EXPIRED_TOKEN);
		} catch (SignatureException e) {
			log.info("시그니처 검증에 실패한 토큰입니다.", e);
			setResponse(response, FAILED_VERIFY_SIGNATURE);
		} catch (JwtException e) {
			log.info("토큰이 올바르지 않습니다.");
			setResponse(response, INVALID_TOKEN);
		}
	}

	private boolean isPass(String path) {
		return
			path.contains("login") ||
				path.contains("kakao") ||
				path.contains("register") ||
				path.contains("ws") ||
				path.isEmpty() ||
				path.equals("/auth/refresh") ||
				path.contains("search") ||
				path.equals("/");
	}

	private boolean isBlocked(String token) {
		return redisTemplate.opsForValue().get("BLOCK:" + token) != null;
	}

	private void setResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setContentType("application/json");
		response.setCharacterEncoding("utf-8");

		objectMapper.writeValue(response.getWriter(), ErrorResponse.from(errorCode));
	}
}