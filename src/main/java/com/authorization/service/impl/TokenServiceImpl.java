package com.authorization.service.impl;

import com.authorization.cache.TokenCache;
import com.authorization.entity.AuthSession;
import com.authorization.entity.AuthSessionLog;
import com.authorization.entity.Client;
import com.authorization.exception.InvalidClientException;
import com.authorization.exception.TokenProcessingFailedException;
import com.authorization.exception.AuthenticationFailedException;
import com.authorization.loggers.Loggers;
import com.authorization.mapper.AuthSessionLogMapper;
import com.authorization.mapper.AuthSessionMapper;
import com.authorization.dto.AuthSessionDTO;
import com.authorization.model.TokenResponse;
import com.authorization.repository.AuthSessionLogRepository;
import com.authorization.repository.AuthSessionRepository;
import com.authorization.repository.ClientRepository;
import com.authorization.service.JwttService;
import com.authorization.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

@Service
public class TokenServiceImpl implements TokenService {

    @Autowired
    private ClientRepository clientRepo;

    @Autowired
    private AuthSessionRepository authSessionRepository;

    @Autowired
    private TokenCache tokenCache;

    @Autowired
    private JwttService jwttService;

    @Autowired
    private JwtDecoder jwtDecoder;

    @Autowired
    private AuthSessionMapper authSessionMapper;

    @Autowired
    private AuthSessionLogMapper authSessionLogMapper;

    @Autowired
    private AuthSessionLogRepository authSessionLogRepository;



    @Override
    public boolean validateToken(String accessTokenUUID) {
        Loggers.AUTHORIZATION_LOGGER.info("Entered validateToken");

        try {
            Instant now = Instant.now();
            long epochSeconds = now.getEpochSecond();
            AuthSessionDTO authSessionDTO = tokenCache.getAuthSessionByToken(accessTokenUUID).orElse(null);

            if (authSessionDTO != null) {
                Loggers.AUTHORIZATION_LOGGER.info(" Session found in Redis for the accessTokenUUID {},authSessionDTO {}",accessTokenUUID,authSessionDTO);
                return true;
            }

            Loggers.AUTHORIZATION_LOGGER.info("accessTokenUUID {} , epochSeconds {}",accessTokenUUID,epochSeconds);
            AuthSession authSession = authSessionRepository.getActiveAuthSessionByAccessTokenUUID(accessTokenUUID,epochSeconds)
                    .orElse(null);

            Loggers.AUTHORIZATION_LOGGER.info("fetch ActiveAuthsession from db is {}",authSession);
            if (authSession == null ) {
                Loggers.AUTHORIZATION_LOGGER.warn("Invalid token: no session found in DB");
                return false;
            }
            return true;

        } catch (Exception ex) {

            Loggers.AUTHORIZATION_LOGGER.error("Token validation failed unexpectedly: {}", ex.getMessage(), ex);
            throw new AuthenticationFailedException("Token validation failed unexpectedly");
        }
    }


    @Override
    public TokenResponse generaterefreshToken(String refreshTokenUUID) {
        Loggers.AUTHORIZATION_LOGGER.info("Entered into generaterefreshToken method ");
        try {
            Instant now = Instant.now();
            long epochSeconds = now.getEpochSecond();

            AuthSession authSession = authSessionRepository.getAuthSessionByRefreshTokenUUID(refreshTokenUUID, epochSeconds)
                    .orElseThrow(() -> new TokenProcessingFailedException("Refresh token has expired"));

            Loggers.AUTHORIZATION_LOGGER.info("fetched authSession {} " , authSession);
            tokenCache.removeAuthSessionByToken(authSession.getAccessTokenUUID());


            String accessTokenUUID = UUID.randomUUID().toString();
            Jwt accessToken = jwttService.generateToken(authSession.getClientId(), "access", 3600);


            Loggers.AUTHORIZATION_LOGGER.info("generated UUID and Jwt Access Token {} -- {} " , accessTokenUUID,accessToken);

            AuthSessionDTO newAuthSessionDTO = AuthSessionDTO.builder()
                    .clientId(authSession.getClientId())
                    .accessTokenUUID(accessTokenUUID)
                    .accessToken(accessToken.getTokenValue())
                    .accessTokenExpiry(accessToken.getExpiresAt().getEpochSecond())
                    .refreshTokenUUID(authSession.getRefreshTokenUUID())
                    .refreshToken(authSession.getRefreshToken())
                    .refreshTokenExpiry(authSession.getRefreshTokenExpiry())
                    .isActive(true)
                    .build();


            AuthSession newAuthSession = authSessionMapper.toEntity(newAuthSessionDTO);
            newAuthSession.setId(authSession.getId());
            newAuthSession.setCreatedAt(authSession.getCreatedAt());
            authSessionRepository.save(newAuthSession);


            AuthSessionLog newAuthSessionLog = authSessionLogMapper.toEntity(newAuthSessionDTO);
            authSessionLogRepository.save(newAuthSessionLog);

            tokenCache.cacheAuthSession(newAuthSessionDTO);

            Loggers.AUTHORIZATION_LOGGER.info("Successfully there is insertion of newSessionDTO {} in Auth and Log table " , newAuthSessionDTO );

            return TokenResponse.builder()
                    .accessToken(newAuthSession.getAccessTokenUUID())
                    .accessTokenExpiresIn((newAuthSession.getAccessTokenExpiry()-epochSeconds))
                    .refreshToken(newAuthSession.getRefreshTokenUUID())
                    .refreshTokenExpiresIn((newAuthSession.getRefreshTokenExpiry()-epochSeconds))
                    .build();
        } catch (Exception ex) {
            Loggers.AUTHORIZATION_LOGGER.error("Error while refreshing access token: {}", ex.getMessage(), ex);
            throw new TokenProcessingFailedException("Failed to refresh access token");
        }
    }

    @Override
    public boolean revokeAccessToken(String accessTokenUUID) {
        Loggers.AUTHORIZATION_LOGGER.info("Entered into revokeAccessToken method ");
        try {

            Instant now = Instant.now();
            long epochSeconds = now.getEpochSecond();

            tokenCache.removeAuthSessionByToken(accessTokenUUID);

            AuthSession authSession = authSessionRepository.getActiveAuthSessionByAccessTokenUUID(accessTokenUUID,epochSeconds)
                    .orElse(null);

            if (authSession == null ) return false;

            authSession.setActive(false);

            authSessionRepository.save(authSession);

            return true;
        } catch (Exception ex) {
            Loggers.AUTHORIZATION_LOGGER.error("Revoke token failed unexpectedly: {}", ex.getMessage(), ex);
            throw new TokenProcessingFailedException("Unexpected error occurred while revoking access token");
        }
    }


    @Override
    public TokenResponse authenticate(String authorizationHeader) {

        Loggers.AUTHORIZATION_LOGGER.info("Entered into authentication method to generate accessToken");
        try {
            if (authorizationHeader == null || !authorizationHeader.startsWith("Basic ")) {
                throw new InvalidClientException("invalid_client", "Missing or invalid Authorization header");
            }

            String base64Credentials = authorizationHeader.substring("Basic ".length());
            String credentials = new String(Base64.getDecoder().decode(base64Credentials), StandardCharsets.UTF_8);

            String[] parts = credentials.split(":", 2);

            if (parts.length != 2) {
                throw new InvalidClientException("invalid_client", "Invalid Authorization format");
            }
            String clientId = parts[0];
            String clientSecret = parts[1];

            Instant now = Instant.now();
            long epochSeconds = now.getEpochSecond();

            Client client = clientRepo.findByClientIdAndClientSecret(clientId,clientSecret)
                    .orElseThrow(() -> new InvalidClientException("invalid_client", "Invalid client credentials"));

            Loggers.AUTHORIZATION_LOGGER.info("fetched Client from db {}", client);

            Jwt accessToken = jwttService.generateToken(clientId, "access", 3600);
            Jwt refreshToken = jwttService.generateToken(clientId, "refresh", 86400);

            String accessTokenUUID = UUID.randomUUID().toString();
            String refreshTokenUUID = UUID.randomUUID().toString();

            Loggers.AUTHORIZATION_LOGGER.info("Created accessTokenUUID {} - refreshToken {}",accessTokenUUID,refreshTokenUUID);

            Long id = authSessionRepository.findIdByClientId(clientId).orElse(null);

            AuthSessionDTO sessionDTO = AuthSessionDTO.builder()
                    .clientId(clientId)
                    .accessTokenUUID(accessTokenUUID)
                    .accessToken(accessToken.getTokenValue())
                    .accessTokenExpiry(accessToken.getExpiresAt().getEpochSecond())
                    .refreshTokenUUID(refreshTokenUUID)
                    .refreshToken(refreshToken.getTokenValue())
                    .refreshTokenExpiry(refreshToken.getExpiresAt().getEpochSecond())
                    .isActive(true)
                    .build();

            Loggers.AUTHORIZATION_LOGGER.info("fetched Id from db {} and generated sessionDTO {}",id,sessionDTO);

            AuthSession authSession = authSessionMapper.toEntity(sessionDTO);
            if (id!=null) authSession.setId(id);
            AuthSessionLog authSessionLog = authSessionLogMapper.toEntity(sessionDTO);
            tokenCache.cacheAuthSession(sessionDTO);

            authSession.setCreatedAt(now);
            authSessionRepository.save(authSession);
            authSessionLogRepository.save(authSessionLog);

            Loggers.AUTHORIZATION_LOGGER.info("Created authSession {} and authSessionLog {}",authSession,authSessionLog);
            return TokenResponse.builder()
                    .accessToken(accessTokenUUID)
                    .accessTokenExpiresIn((authSession.getAccessTokenExpiry()-epochSeconds))
                    .refreshToken(refreshTokenUUID)
                    .refreshTokenExpiresIn((authSession.getRefreshTokenExpiry()-epochSeconds))
                    .build();
        } catch (Exception ex) {
            Loggers.AUTHORIZATION_LOGGER.error("Authentication error: {}", ex.getMessage(), ex);
            throw new AuthenticationFailedException(ex.getMessage());
        }
    }

    @Override
    public AuthSessionDTO getActiveAuthSession(String accessTokenUUID) {
        Instant now = Instant.now();
        long epochSeconds = now.getEpochSecond();

        Optional<AuthSessionDTO> sessionOpt = tokenCache.getAuthSessionByToken(accessTokenUUID);

        Loggers.AUTHORIZATION_LOGGER.info("fetched AuthSessionDTO {} from redis " , sessionOpt);
        if (sessionOpt.isPresent()) {
            return sessionOpt.get();
        }

        Optional<AuthSession> authSessionOpt = authSessionRepository.getActiveAuthSessionByAccessTokenUUID(accessTokenUUID,epochSeconds);

        Loggers.AUTHORIZATION_LOGGER.info("fetched AuthSession {} from db " , authSessionOpt);

        if (authSessionOpt.isPresent()){
            return authSessionMapper.toAuthSessionDTO(authSessionOpt.get());
        }

        return null;
    }


}
