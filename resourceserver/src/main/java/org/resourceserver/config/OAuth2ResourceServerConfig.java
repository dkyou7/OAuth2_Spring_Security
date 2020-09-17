package org.resourceserver.config;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

/**
 * ResourceServerConfigurerAdapter를 상속받아 구현하고, @EnableResourceServer를 선언함으로써
 * OAuth2AuthenticationProcessingFilter를 추가하는 몇 가지 설정이 임포트되어 리소스 서버의 액세스토큰
 * 유효성 검증이 수행된다.
 *
 * OAuth2AuthenticationProcessingFilter는 "/**"패턴과 매칭되는 엔드포인트에 대한
 * 액세스 토큰 유효성 검사 프로세스 시작을 담당하는 필터이다.(헤더에서 Bearer 토큰을 분리하여 인증한다.)
 * Authorization 헤더(bearer)에서 토큰을 추출하여 없으면 QueryString(access_token)을 뒤져본다.
 * 토큰을 찾았으면 Token value를 principal, "" 빈문자열을 credentials로 넣은 Authentication객체를
 * 리턴한다. 그리고 HttpServletRequest에 Token Type : Bearer Token value : token string을 넣어
 * tokenvalue,tokentype,사용자remoteaddress,http sessionid 등의 정보를 담은 OAuth2AuthenticationDetails객체에
 * 넣어서 AbstractAuthenticationToken객체의 details Object에 넣어준다. 그리고 해당 객체를 매개변수로
 * AuthenticationManager.authenticate 메소드로 토큰의 유효성을 검증한다.(jwt 토큰이라면 자체 검증)
 * 만약 토큰 유효성 검사에서 실패하면 별도 리다이렉트 없이 예외 정보가 담긴 응답을 받는다.
 */
@Configuration
@EnableResourceServer
public class OAuth2ResourceServerConfig extends ResourceServerConfigurerAdapter{
	
	/**
	 * 리소스 서버 엔드포인트 보호를 위한 보안 룰 적용
	 */
	@Override
	public void configure(HttpSecurity http) throws Exception {
		// TODO Auto-generated method stub
		http.authorizeRequests()
			.anyRequest().authenticated()
			.and()
			//OAuth2.0 토큰 인증을 받아야하는 요청들 규칙정리
			.requestMatchers().antMatchers("/**")
		;
	}
	
	/**
	 * ResourceTokenService는 Resource Server가 액세스 토큰의 유효성을 검사하기 위해
	 * 사용된다. 해당 서비스 클래스는 tokenStore()에 어떠한 스토어가 설정되냐에 의존적으로 수행된다.
	 */
	@Override
	public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
		resources
			.tokenStore(tokenStore())
//			.tokenServices(tokenServices)
//			.tokenExtractor(new BearerTokenExtractor())
//			.authenticationManager(new OAuth2AuthenticationManager())
			.authenticationEntryPoint(new AuthenticationEntryPoint() {
				@Override
				public void commence(HttpServletRequest request, HttpServletResponse response,
						AuthenticationException authException) throws IOException, ServletException {
					PrintWriter writer = response.getWriter();
					writer.println("Invalid token !");
					
				}
			})
			.accessDeniedHandler(new AccessDeniedHandler() {
				
				@Override
				public void handle(HttpServletRequest request, HttpServletResponse response,
						AccessDeniedException accessDeniedException) throws IOException, ServletException {
					PrintWriter writer = response.getWriter();
					writer.println("Access Denied !");
				}
			});
	}
	
	@Bean
	public JwtAccessTokenConverter jwtAccessTokenConverter() {
		JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
		converter.setSigningKey("non-prod-signature");
		return converter;
	}
    
	@Bean
	public TokenStore tokenStore() {
		JwtTokenStore tokenStore = new JwtTokenStore(jwtAccessTokenConverter());
		return tokenStore;
	}
}
