package org.authorizationserver.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.sql.Clob;

@Entity
@Getter @Setter
public class OauthAccessToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tokenId;
    private String token;
    private String authenticationId;
    private String username;
    private Clob authentication;
    private String refreshToken;

}
