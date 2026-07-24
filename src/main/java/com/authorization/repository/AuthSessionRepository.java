package com.authorization.repository;

import com.authorization.entity.AuthSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface AuthSessionRepository extends JpaRepository<AuthSession, Long> {

    @Query("SELECT as FROM AuthSession as WHERE as.clientId = :clientId and as.refreshTokenExpiry > :now")
    Optional<AuthSession> getAuthSessionByClientId(@Param("clientId") String clientId, @Param("now") LocalDateTime now);

    @Query(value = "SELECT * FROM auth_session a " +
            "WHERE a.access_token = :accessToken " +
            "AND a.refreshTokenExpiry > :now " +
            "AND a.isActive = true " +
            "ORDER BY a.id DESC LIMIT 1",
            nativeQuery = true)
    Optional<AuthSession> getAuthSessionByAccessToken(@Param("accessToken") String accessToken, @Param("now") LocalDateTime now);

    Optional<AuthSession> getAuthSessionByAccessTokenAndClientId(@Param("accessToken") String accessToken,@Param("clientId") String clientId);

    @Query(value = "SELECT * FROM auth_session a " +
            "WHERE a.access_tokenuuid = :accessTokenUUID " +
            "AND a.access_token_expiry > :epochSeconds " +
            "AND a.is_active = true",
            nativeQuery = true)
    Optional<AuthSession> getActiveAuthSessionByAccessTokenUUID(@Param("accessTokenUUID") String accessTokenUUID,@Param("epochSeconds") long epochSeconds);

    @Query(value = "SELECT * FROM auth_session a " +
            "WHERE a.refresh_token = :refreshToken " +
            "AND a.refreshTokenExpiry > :now " +
            "AND a.isActive = true " +
            "ORDER BY a.id DESC LIMIT 1",
            nativeQuery = true)
    Optional<AuthSession> getAuthSessionByRefreshToken(@Param("refreshToken") String refreshToken, @Param("now") LocalDateTime now);

    @Query(value = "SELECT * FROM auth_session a " +
            "WHERE a.refresh_tokenuuid = :refreshTokenUUID " +
            "AND a.refresh_token_expiry > :epochSeconds " ,
            nativeQuery = true)
    Optional<AuthSession> getAuthSessionByRefreshTokenUUID(@Param("refreshTokenUUID") String refreshTokenUUID,@Param("epochSeconds") long epochSeconds);


    Optional<AuthSession> findByAccessTokenUUIDAndAccessTokenExpiryGreaterThan(
            String accessTokenUUID, Long epochSeconds);

    @Query("SELECT a.id FROM AuthSession a WHERE a.clientId = :clientId")
    Optional<Long> findIdByClientId(@Param("clientId") String clientId);
}
