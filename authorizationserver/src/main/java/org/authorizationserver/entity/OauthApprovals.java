package org.authorizationserver.entity;

import lombok.Getter;
import lombok.Setter;

import javax.naming.Name;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
public class OauthApprovals {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "USERID")
    private String userId;

    @Column(name = "CLIENTID")
    private String clientId;

    @Column(name = "SCOPE")
    private String scope;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "EXPIRESAT")
    private LocalDateTime expiresAt;

    @Column(name = "LASTMODIFIEDAT")
    private LocalDateTime lastModifiedat;

}
