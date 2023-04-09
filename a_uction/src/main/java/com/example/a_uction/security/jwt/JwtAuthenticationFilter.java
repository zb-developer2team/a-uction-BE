package com.example.a_uction.security.jwt;

import static com.example.a_uction.exception.constants.ErrorCode.LOGOUT_USER_ERROR;

import com.example.a_uction.exception.AuctionException;
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
	private final RedisTemplate redisTemplate;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		String path = request.getServletPath();
		log.info("REQUEST [ SERVLET_PATH : {} ]", path);
		if (isPass(path)) {
			filterChain.doFilter(request, response);
			return;
		}

		String token = provider.resolveTokenFromRequest(request);

		if (request.getRequestURI().contains("login") ||
			request.getRequestURI().contains("register")) {
			filterChain.doFilter(request, response);
			return;
		}

		if (provider.validateToken(token)) {
			if (!this.isBlocked(token)) {
				UsernamePasswordAuthenticationToken authentication =
					provider.getAuthentication(token);

				authentication.setDetails(
					new WebAuthenticationDetailsSource().buildDetails(request));

				SecurityContextHolder.getContext()
					.setAuthentication(authentication);
			} else {
				throw new AuctionException(LOGOUT_USER_ERROR);
			}
		}
		filterChain.doFilter(request, response);
	}

	private boolean isPass(String path) {
		return
			path.contains("login") ||
				path.contains("oauth/kakao") ||
				path.contains("register") ||
				path.contains("auction") ||
				path.equals("/");
	}

	private boolean isBlocked(String token) {
		if (redisTemplate.opsForValue().get("BLOCK:" + token) != null) {
			return true;
		}
		return false;
	}
}
