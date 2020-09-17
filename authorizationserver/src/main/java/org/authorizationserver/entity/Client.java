package org.authorizationserver.entity;

import org.authorizationserver.constrant.ClientType;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;

import lombok.Getter;

/**
 * client 인증에 사용되는 객체이다.
 * ClientDetailsService에서 load 하면 반환하는 객체.
 */
public class Client extends BaseClientDetails{

	private static final long serialVersionUID = 5840531070411146325L;
	
	@Getter
	private ClientType clientType;


	public void setClientType(ClientType clientType) {
		this.clientType = clientType;
		this.addAdditionalInformation("client_type", clientType.name());
	}

}
