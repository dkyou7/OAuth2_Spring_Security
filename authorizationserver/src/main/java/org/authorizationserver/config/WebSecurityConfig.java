package org.authorizationserver.config;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collections;

import org.authorizationserver.security.CustomAuthenticationFailureHandler;
import org.authorizationserver.security.CustomAuthenticationSuccessHandler;
import org.authorizationserver.security.ResourceOwnerAuthenticationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * 스프링 시큐리티 관련 소스
 */
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter{

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired private UserDetailsService userDetailsService;
	@Autowired private PasswordEncoder passwordEncoder;

	/**
	 * 2. 두번째로 도는 함수. 권한매핑인가?
	 */
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		log.info("========== WebSecurityConfig :: configure(AuthenticationManagerBuilder auth) start ==========");
		auth.authenticationProvider(authenticationProvider());
		log.info("========== WebSecurityConfig :: configure(AuthenticationManagerBuilder auth) end ==========");
	}

	/**
	 * 6. 무시할 static files들을 정의.
	 */
	@Override
	public void configure(WebSecurity web) throws Exception {
		log.info("========== WebSecurityConfig :: configure(WebSecurity web) start ==========");

		web.ignoring()
		   .antMatchers("/css/**")
		   .antMatchers("/vendor/**")
		   .antMatchers("/js/**")
		   .antMatchers("/favicon*/**")
		   .antMatchers("/img/**")
		;

		log.info("========== WebSecurityConfig :: configure(WebSecurity web) end ==========");
	}

	/**
	 * 4. 시큐리티 설정 필터를 돈다.
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		log.info("========== WebSecurityConfig :: configure(HttpSecurity http) start ==========");

		http
			.cors()
			.and()
		.authorizeRequests()
			.antMatchers("/login*/**").permitAll()
			.antMatchers("/error**").permitAll()
			.anyRequest().authenticated()
		.and().csrf()
			  .disable()
		.addFilter(authenticationFilter())
		.exceptionHandling()
			  .authenticationEntryPoint(authenticationEntryPoint())
			  .accessDeniedHandler((request,response,exception)->{
				  response.setContentType("application/json;charset=UTF-8");
		          response.setHeader("Cache-Control", "no-cache");
		          
		          PrintWriter writer = response.getWriter();
		          writer.println(new AccessDeniedException("access denied !"));
			  })
		;

		log.info("========== WebSecurityConfig :: HttpSecurity config end ==========");

	}

	/**
	 * 7. Cors 설정.
	 */
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		log.info("========== WebSecurityConfig :: corsConfigurationSource() bean start ==========");

		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(Collections.singletonList("*"));
		configuration.setAllowedMethods(Collections.singletonList("*"));
		configuration.setAllowedHeaders(Collections.singletonList("*"));
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);

		log.info("========== WebSecurityConfig :: corsConfigurationSource() bean end ==========");
		
		return source;
	}

	/**
	 * 3. 유저 권한이 계속되는 것 같다.
	 */
	@Bean
	public AuthenticationProvider authenticationProvider() {
		log.info("========== WebSecurityConfig :: authenticationProvider() bean start ==========");

		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(userDetailsService);
		authenticationProvider.setPasswordEncoder(passwordEncoder);

		log.info("========== WebSecurityConfig :: authenticationProvider() bean end ==========");
		return authenticationProvider;
	}

	/**
	 * 	1. 제일 처음으로 시작한다. filter 설정을 도는듯.
 	 */
	@Bean
	public ResourceOwnerAuthenticationFilter authenticationFilter() throws Exception {
		log.info("========== WebSecurityConfig :: authenticationFilter() bean start ==========");

		/**
		 * 권한을 체크하는 filter
		 */
		ResourceOwnerAuthenticationFilter filter = new ResourceOwnerAuthenticationFilter(authenticationManager());
		filter.setFilterProcessesUrl("/login");
		filter.setUsernameParameter("username");
		filter.setPasswordParameter("password");
		
		filter.setAuthenticationSuccessHandler(authenticationSuccessHandler());	// 성공시 작업
		filter.setAuthenticationFailureHandler(authenticationFailureHandler());	// 실패시 작업
		
		filter.afterPropertiesSet();

		log.info("========== WebSecurityConfig :: authenticationFilter() bean end ==========");
		return filter;
	}

	/**
	 * 5. Spring Security 에서 로그인 페이지로 리다이렉트 시켜줄 Entry point 빈.
	 * 권한이 없는 사용자가 접근하려 할 때, 해당 객체가 로그인 페이지로 리다이렉트 시켜주는 역할을 수행한다.
	 */
	@Bean
	public AuthenticationEntryPoint authenticationEntryPoint() {
		log.info("========== WebSecurityConfig :: authenticationEntryPoint() bean start ==========");
		log.info("========== WebSecurityConfig :: authenticationEntryPoint() bean end ==========");
		return new LoginUrlAuthenticationEntryPoint("/loginPage");
	}

	/**
	 * 로그인 성공시 처리
	 */
	@Bean
	public AuthenticationSuccessHandler authenticationSuccessHandler() {
		log.info("========== WebSecurityConfig :: authenticationSuccessHandler() bean start ==========");

		CustomAuthenticationSuccessHandler successHandler = new CustomAuthenticationSuccessHandler();
		successHandler.setDefaultTargetUrl("/client/dashboard");

		log.info("========== WebSecurityConfig :: authenticationSuccessHandler() bean end ==========");
		return successHandler;
	}

	/**
	 * 로그인 실패시 처리
	 */
	@Bean
	public AuthenticationFailureHandler authenticationFailureHandler() {
		log.info("========== WebSecurityConfig :: authenticationFailureHandler() bean start ==========");

		CustomAuthenticationFailureHandler failureHandler = new CustomAuthenticationFailureHandler();
		failureHandler.setDefaultFailureUrl("/loginPage?error=loginfail");

		log.info("========== WebSecurityConfig :: authenticationFailureHandler() bean end ==========");
		return failureHandler;
	}
}
