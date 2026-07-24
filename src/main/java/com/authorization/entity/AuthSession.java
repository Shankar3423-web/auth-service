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


@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name= "auth_session")
@Builder
public class AuthSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true,nullable = false)
    private String clientId;

    @Column(nullable = false)
    private String accessTokenUUID;

    @Column(nullable = false,length = 4096)
    private String accessToken;

    private long accessTokenExpiry;

    @Column(nullable = false)
    private String refreshTokenUUID;

    @Column(nullable = false,length = 4096)
    private String refreshToken;

    private long refreshTokenExpiry;

    @Column(nullable = false)
    @Builder.Default
    private boolean isActive = true;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

}
