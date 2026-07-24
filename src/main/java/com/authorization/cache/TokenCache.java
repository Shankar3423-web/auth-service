package com.authorization.cache;

import com.authorization.loggers.Loggers;
import com.authorization.dto.AuthSessionDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TokenCache {
    private final RedisTemplate<String, Object> redisTemplate;

    private final ObjectMapper objectMapper;

    public void cacheAuthSession(AuthSessionDTO sessionDTO) {
        try {

            String HashKey = sessionDTO.getAccessTokenUUID();

            Instant now = Instant.now();

            long epochSeconds = now.getEpochSecond();


            long ttlSeconds  = (sessionDTO.getAccessTokenExpiry()-epochSeconds);

            redisTemplate.opsForValue().set(HashKey,sessionDTO,Duration.ofSeconds(3600));

            Loggers.AUTHORIZATION_LOGGER.info("Cached active AuthSession for clientId={}, ttl = {}", sessionDTO.getClientId(),ttlSeconds);
        } catch (Exception e) {
            Loggers.AUTHORIZATION_LOGGER.error("Error caching AuthSession for {}: {}", sessionDTO.getClientId(), e.getMessage(), e);
        }
    }


    public Optional<AuthSessionDTO> getAuthSessionByToken(String accessTokenUUID) {
        try {
            String key =  accessTokenUUID;
            Object value = redisTemplate.opsForValue().get(key);

            if (value == null ) return Optional.empty();

            AuthSessionDTO dto = objectMapper.convertValue(value, AuthSessionDTO.class);

            Long ttlSeconds = redisTemplate.getExpire(key, TimeUnit.SECONDS);

            if ( ttlSeconds < 0) { removeAuthSessionByToken(dto.getAccessTokenUUID()); ;return Optional.empty();}


            return Optional.of(dto);
        } catch (Exception e) {
            Loggers.AUTHORIZATION_LOGGER.error("Error fetching AuthSession from Redis for {}: {}", accessTokenUUID, e.getMessage(), e);
            return Optional.empty();
        }
    }


    public boolean removeAuthSessionByToken(String accessTokenUUID) {
        try {
            redisTemplate.delete(accessTokenUUID);
            Loggers.AUTHORIZATION_LOGGER.info("Removed AuthSession from cache for accessToken={}", accessTokenUUID);
            return true;
        } catch (Exception e) {
            Loggers.AUTHORIZATION_LOGGER.error("Error removing AuthSession for {}: {}", accessTokenUUID, e.getMessage(), e);
            return false;
        }
    }

}
