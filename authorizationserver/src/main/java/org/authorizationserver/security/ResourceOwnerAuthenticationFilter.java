package org.authorizationserver.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.extern.slf4j.Slf4j;

/**
 * form login 인증을 담당하는 filter
 */
@Slf4j
public class ResourceOwnerAuthenticationFilter extends UsernamePasswordAuthenticationFilter{
	
	private boolean postOnly = true;
	
	public ResourceOwnerAuthenticationFilter(AuthenticationManager authenticationManager) {
		super.setAuthenticationManager(authenticationManager);
	}

	/**
	 * 인증 프로세스 이전에 요청에서 사용자 정보를 가져와서
	 * Authentication 객체를 인증 프로세스 객체에게 전달하는 역할
	 */
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		
		log.info("ResourceOwnerAuthenticationFilter.attemptAuthentication ::::");
		
		/**
		 * POST로 넘어왔는지 체크
		 */
		if(postOnly && !request.getMethod().equals("POST")) {
			throw new AuthenticationServiceException(
					"Authentication method not supported: " + request.getMethod());
		}
		
		String username = obtainUsername(request);
		String password = obtainPassword(request);
		
		if(StringUtils.isEmpty(username)) {
			username = "";
		}
		if(StringUtils.isEmpty(password)) {
			password = "";
		}
		
		username = username.trim();
		
		UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);
		
		setDetails(request, authRequest);
		
		return this.getAuthenticationManager().authenticate(authRequest);
	}
	
	
}
