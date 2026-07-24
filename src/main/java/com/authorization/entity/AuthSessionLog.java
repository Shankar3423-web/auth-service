package com.authorization.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthSessionLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String clientId;

    @Column(unique = true,nullable = false)
    private String accessTokenUUID;

    @Column(nullable = false,length = 4096)
    private String accessToken;

    private long accessTokenExpiry;

    @Column(nullable = false)
    private String refreshTokenUUID;

    @Column(nullable = false,length = 4096)
    private String refreshToken;

    private long refreshTokenExpiry;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

}
