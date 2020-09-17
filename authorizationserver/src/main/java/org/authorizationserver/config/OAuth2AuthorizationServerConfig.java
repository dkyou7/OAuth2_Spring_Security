package org.authorizationserver.config;

import java.io.PrintWriter;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationService;
import org.springframework.security.oauth2.provider.approval.JdbcApprovalStore;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.code.JdbcAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;


/**
 * Authorization Server Config
 * 인증서버 활성화 부분입니다.
 */
@Configuration
@EnableAuthorizationServer
public class OAuth2AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	@Autowired private DataSource dataSource;
	@Autowired@Qualifier("clientDetailsServiceImpl") private ClientDetailsService clientDetailsService;
	@Autowired private UserDetailsService userDetailsService;
	
	@Override
	public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
		log.info("========== OAuth2AuthorizationServerConfig :: configure(AuthorizationServerSecurityConfigurer security) start ==========");

		security.accessDeniedHandler((request, response, exception)->{
										response.setContentType("application/json;charset=UTF-8");
							            response.setHeader("Cache-Control", "no-cache");
							            PrintWriter writer = response.getWriter();
							            writer.println(new AccessDeniedException("access denied !"));
									})
		.authenticationEntryPoint((request, response, exception)->{
									response.setContentType("application/json;charset=UTF-8");
						            response.setHeader("Cache-Control", "no-cache");
						            PrintWriter writer = response.getWriter();
						            writer.println(new AccessDeniedException("access denied !"));
								})
		;
		log.info("========== OAuth2AuthorizationServerConfig :: configure(AuthorizationServerSecurityConfigurer security) end ==========");
	}
	
	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		/**
		 * 클라이언트를 DB에서 관리하기 위해 DataSource 주입.
		 * UserDetailsService와 동일한 역할을 하는 객체.
		 */
		log.info("========== OAuth2AuthorizationServerConfig :: configure(ClientDetailsServiceConfigurer clients) start ==========");
		clients.withClientDetails(clientDetailsService);
		log.info("========== OAuth2AuthorizationServerConfig :: configure(ClientDetailsServiceConfigurer clients) end ==========");
	}

	/**
	 * 1. OAuth Endpoint 관련 설정이 시작된다.
	 */
	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		log.info("========== OAuth2AuthorizationServerConfig :: AuthorizationServerEndpointsConfigurer configure start ==========");
		endpoints
			.userDetailsService(userDetailsService) //refresh token 발급을 위해서는 UserDetailsService(AuthenticationManager authenticate()에서 사용)필요
			.authorizationCodeServices(new JdbcAuthorizationCodeServices(dataSource)) //authorization code를 DB로 관리 코드 테이블의 authentication은 blob데이터타입으로..
			.approvalStore(approvalStore()) //리소스 소유자의 승인을 추가, 검색, 취소하기 위한 메소드를 정의
			.tokenStore(tokenStore()) //토큰과 관련된 인증 데이터를 저장, 검색, 제거, 읽기를 정의
			.accessTokenConverter(accessTokenConverter())
			;
		log.info("========== OAuth2AuthorizationServerConfig :: AuthorizationServerEndpointsConfigurer configure end ==========");
	}
	
	@Bean
	public JwtTokenStore tokenStore() {
		return new JwtTokenStore(accessTokenConverter());
	}
	
	@Bean
	public JdbcApprovalStore approvalStore() {
		return new JdbcApprovalStore(dataSource);
	}
	@Bean
	public JwtAccessTokenConverter accessTokenConverter() {
		JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
		converter.setSigningKey("non-prod-signature");
		
		return converter;
	}
	
	/*
	 * 새로운 클라이언트 등록을 위한 빈
	 */
	@Bean
	public ClientRegistrationService clientRegistrationService() {
		return new JdbcClientDetailsService(dataSource);
	}
}
