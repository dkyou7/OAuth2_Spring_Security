package org.authorizationserver.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 뷰 랜더링을 위한 컨트롤러 설정.
 * 로그인 페이지로 이동시켜준다.
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer{

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		log.info("========== WebMvcConfig :: addViewControllers(ViewControllerRegistry registry) start ==========");

		registry.addViewController("/loginPage")
				.setViewName("login");

		log.info("========== WebMvcConfig :: addViewControllers(ViewControllerRegistry registry) end ==========");
	}

}
