package org.authorizationserver.service;

import java.util.List;

import javax.sql.DataSource;

import org.authorizationserver.entity.OauthClientDetails;
import org.authorizationserver.repository.OauthClientDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.ClientAlreadyExistsException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.NoSuchClientException;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

/**
 * 클라이언트 인증시 사용되는 서비스 클래스
 * DB에서 ClientDetails 객체를 가져온다.
 * UserDetailsService와 동일한 역할 수행
 */
@Slf4j
@Primary
@Service
public class ClientDetailsServiceImpl extends JdbcClientDetailsService {
	
	public ClientDetailsServiceImpl(DataSource dataSource) {
		super(dataSource);
		// TODO Auto-generated constructor stub
	}
	
	
	@Override
	public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
		log.info("ClientDetailsServiceImpl.loadClientByClientId :::: {}",clientId);
		return super.loadClientByClientId(clientId);
	}

	@Override
	public void addClientDetails(ClientDetails clientDetails) throws ClientAlreadyExistsException {
		log.info("ClientDetailsServiceImpl.addClientDetails :::: {}",clientDetails.toString());
		super.addClientDetails(clientDetails);
	}

	@Override
	public void updateClientDetails(ClientDetails clientDetails) throws NoSuchClientException {
		log.info("ClientDetailsServiceImpl.updateClientDetails :::: {}",clientDetails.toString());
		super.updateClientDetails(clientDetails);
	}

	@Override
	public void updateClientSecret(String clientId, String secret) throws NoSuchClientException {
		log.info("ClientDetailsServiceImpl.updateClientSecret :::: {},{}",clientId,secret);
		super.updateClientSecret(clientId, secret);
	}

	@Override
	public void removeClientDetails(String clientId) throws NoSuchClientException {
		log.info("ClientDetailsServiceImpl.removeClientDetails :::: {}",clientId);
		super.removeClientDetails(clientId);
	}

	@Override
	public List<ClientDetails> listClientDetails() {
		List<ClientDetails> list = super.listClientDetails();
		log.info("ClientDetailsServiceImpl.listClientDetails :::: count = {}",list.size());
		return list;
	}

	@Autowired
	private OauthClientDetailsRepository repository;

	@Transactional
	public List<OauthClientDetails> findAll() {
		return repository.findAll();
	}
}
