package com.certimetergroup.talentos.bffwebapp.service;

import com.certimetergroup.talentos.commons.exception.FailureException;
import com.certimetergroup.talentos.commons.enumeration.ResponseEnum;
import com.certimetergroup.talentos.commons.response.authentication.AccAndRefresh;
import com.certimetergroup.talentos.commons.response.dto.user.UserLightDto;
import com.certimetergroup.talentos.commons.utility.HttpHeaderUtil;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Service
public class JwtService {
    private final String issuer;
    private final SecretKey accessSecretKey;
    private final SecretKey refreshSecretKey;
    private final long accessExpirationSeconds;
    private final long refreshExpirationSeconds;

    public JwtService(@Value("${security.jwt.issuer}") String issuer,
                      @Value("${security.jwt.access.key}") String accessKey,
                      @Value("${security.jwt.refresh.key}") String refreshKey,
                      @Value("${security.jwt.access-expiration-seconds}") long accessExpirationSeconds,
                      @Value("${security.jwt.refresh-expiration-minutes}") long refreshExpirationSeconds) {
        this.issuer = issuer;
        this.accessSecretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(accessKey));
        this.refreshSecretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(refreshKey));
        this.accessExpirationSeconds = accessExpirationSeconds;
        this.refreshExpirationSeconds = refreshExpirationSeconds;
    }

    public AccAndRefresh refreshTokens(String accessToken, String refreshToken, UserLightDto userLightDto) {
        accessToken = HttpHeaderUtil.sanitizeAccessToken(accessToken);

        if (!validateTokensClaims(accessToken, userLightDto.getRefreshToken())) {
            throw new FailureException(ResponseEnum.JWT_INVALID);
        }

        if (!isAccessTokenExpired(accessToken)) {
            return AccAndRefresh.builder().accessToken(accessToken).refreshToken(refreshToken).build();
        }

        String userId = String.valueOf(userLightDto.getUserId());
        String username = userLightDto.getUsername();
        String role = userLightDto.getRole().name();

        if (!isRefreshTokenExpiring(refreshToken)) {
            String uuid = getClaimFromRefreshToken(refreshToken, Claims.ID, String.class);
            accessToken = generateAccessToken(uuid, userId, username, role);
        } else {
            String uuid = UUID.randomUUID().toString();
            accessToken = generateAccessToken(uuid, userId, username, role);
            refreshToken = generateRefreshToken(uuid, userId, username);
            userLightDto.setRefreshToken(refreshToken);
        }

        return AccAndRefresh.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public String[] generateLoginTokens(UserLightDto userLightDto) {
        String uuid;
        String oldRefreshToken = userLightDto.getRefreshToken();
        if (oldRefreshToken != null && !oldRefreshToken.isBlank() && !this.isRefreshTokenExpiring(userLightDto.getRefreshToken())) {
            uuid = this.getClaimFromRefreshToken(oldRefreshToken, Claims.ID, String.class );
            return new String[]{
                    generateAccessToken(uuid, userLightDto.getUserId().toString(), userLightDto.getUsername(), userLightDto.getRole().name()),
                    oldRefreshToken
            };
        }

        uuid = UUID.randomUUID().toString();
        String accessToken = generateAccessToken(uuid, userLightDto.getUserId().toString(), userLightDto.getUsername(), userLightDto.getRole().name());
        String refreshToken = generateRefreshToken(uuid, userLightDto.getUserId().toString(), userLightDto.getUsername());
        userLightDto.setRefreshToken(refreshToken);

        return new String[]{
                accessToken,
                refreshToken
        };
    }

    public String generateAccessToken(String uuid, String userId, String username, String role) {
        Instant now = Instant.now();
        return Jwts.builder()
                .id(uuid)
                .subject(userId)
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(accessExpirationSeconds)))
                .claim("username", username)
                .claim("role", role)
                .signWith(accessSecretKey, Jwts.SIG.HS256)
                .compact();
    }

    public String generateRefreshToken(String uuid, String userId, String username) {
        Instant now = Instant.now();
        return Jwts.builder()
                .id(uuid)
                .subject(userId)
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .claim("username", username)
                .expiration(Date.from(now.plusSeconds(refreshExpirationSeconds)))
                .signWith(refreshSecretKey, Jwts.SIG.HS256)
                .compact();
    }

    public boolean validateTokensClaims(String accessToken, String refreshTokens) {
        boolean uuidMatch = Objects.equals(
                getClaimFromAccessToken(accessToken, Claims.ID, String.class),
                getClaimFromRefreshToken(refreshTokens, Claims.ID, String.class)
        );
        boolean usernameMatch = Objects.equals(
                getClaimFromAccessToken(accessToken, "username", String.class),
                getClaimFromRefreshToken(refreshTokens, "username", String.class)
        );
        boolean userIdMatch = Objects.equals(
                getClaimFromAccessToken(accessToken, Claims.SUBJECT, String.class),
                getClaimFromRefreshToken(refreshTokens, Claims.SUBJECT, String.class)
        );
        boolean tokenIssuer = Objects.equals(
                getClaimFromAccessToken(accessToken, Claims.ISSUER, String.class),
                getClaimFromRefreshToken(refreshTokens, Claims.ISSUER, String.class)
        );
        boolean tokenIssuerIsUserApi = Objects.equals(
                getClaimFromAccessToken(accessToken, Claims.ISSUER, String.class),
                issuer
        );
        return uuidMatch && usernameMatch && userIdMatch && tokenIssuer && tokenIssuerIsUserApi;
    }

    public boolean isAccessTokenExpired(String accessToken) {
        try {
            this.validateToken(accessSecretKey, accessToken);
            return false;
        } catch (ExpiredJwtException _) {
            return true;
        }
    }

    public boolean isRefreshTokenExpiring(String refreshToken) {
        try {
            this.validateToken(refreshSecretKey, refreshToken);
            Date expiration = this.getClaimFromRefreshToken(refreshToken, Claims.EXPIRATION, Date.class);
            Instant expInstant = expiration.toInstant();
            Instant threshold = Instant.now().plusSeconds(5 * (long) 60);
            return expInstant.isBefore(threshold);
        } catch (ExpiredJwtException _) {
            return true;
        }
    }

    public void validateToken(SecretKey secretKey, String token) {
        try {
            Jwts.parser().requireIssuer(issuer).verifyWith(secretKey).build().parseSignedClaims(token);
        } catch (PrematureJwtException _) {
            throw new FailureException(ResponseEnum.JWT_NOT_YET_VALID);
        } catch (SignatureException _) {
            throw new FailureException(ResponseEnum.JWT_INVALID_SIGNATURE);
        } catch (MalformedJwtException _) {
            throw new FailureException(ResponseEnum.JWT_MALFORMED);
        } catch (UnsupportedJwtException _) {
            throw new FailureException(ResponseEnum.JWT_UNSUPPORTED);
        } catch (IncorrectClaimException _) {
            throw new FailureException(ResponseEnum.JWT_INCORRECT_CLAIMS);
        } catch (IllegalArgumentException _) {
            throw new FailureException(ResponseEnum.JWT_INVALID);
        }
    }

    public <T> T getClaimFromAccessToken(String accessToken, String fieldName, Class<T> fieldClass){
        try {
            this.validateToken(accessSecretKey, accessToken);
            return this.getClaimFromToken(accessSecretKey, accessToken, fieldName, fieldClass);
        } catch (ExpiredJwtException exception) {
            return exception.getClaims().get(fieldName, fieldClass);
        }
    }

    public <T> T getClaimFromRefreshToken(String refreshToken, String fieldName, Class<T> fieldClass){
        try {
            this.validateToken(refreshSecretKey, refreshToken);
            return this.getClaimFromToken(refreshSecretKey, refreshToken, fieldName, fieldClass);
        } catch (ExpiredJwtException exception) {
            return exception.getClaims().get(fieldName, fieldClass);
        }
    }

    public <T> T getClaimFromToken(SecretKey secretkey, String token, String fieldName, Class<T> fieldClass) {
        Jws<Claims> claims = Jwts.parser().verifyWith(secretkey).build().parseSignedClaims(token);
        return claims.getPayload().get(fieldName, fieldClass);
    }
}

