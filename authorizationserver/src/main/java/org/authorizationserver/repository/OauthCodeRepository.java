package org.authorizationserver.repository;

import org.authorizationserver.entity.OauthCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OauthCodeRepository extends JpaRepository<OauthCode,Long> {
}
