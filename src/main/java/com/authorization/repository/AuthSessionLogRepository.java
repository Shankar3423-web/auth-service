package com.authorization.repository;

import com.authorization.entity.AuthSessionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthSessionLogRepository extends JpaRepository<AuthSessionLog, Long> {

    Optional<AuthSessionLog> findByClientIdAndAccessToken(String clientId,String accessToken);

    Optional<AuthSessionLog> findByAccessToken(String accessToken );
}
