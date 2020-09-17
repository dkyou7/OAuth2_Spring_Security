package org.authorizationserver.repository;

import org.authorizationserver.entity.OauthClientDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OauthClientDetailsRepository extends JpaRepository<OauthClientDetails,Long> {
}
