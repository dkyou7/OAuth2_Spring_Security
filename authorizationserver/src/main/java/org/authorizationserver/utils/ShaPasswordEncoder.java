package org.authorizationserver.utils;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * 인증시 비밀번호와 DB에서 불러온 암호화된 패스워드를 비교하는 역할 수행한다.
 */
@Slf4j
@Component
public class ShaPasswordEncoder implements PasswordEncoder{

	@Override
	public String encode(CharSequence rawPassword) {
		log.info("ShaPasswordEncoder.encode :::: {}",rawPassword);
		return Crypto.sha256(rawPassword.toString());
	}

	@Override
	public boolean matches(CharSequence rawPassword, String encodedPassword) {
		log.info("ShaPasswordEncoder.matches :::: {}<->{}",rawPassword,encodedPassword);
		return Crypto.sha256(rawPassword.toString()).equals(encodedPassword);
	}
	
	
}
