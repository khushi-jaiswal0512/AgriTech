package com.agritech.auth.service.impl;

import com.agritech.auth.dto.request.LoginRequest;
import com.agritech.auth.dto.request.RefreshTokenRequest;
import com.agritech.auth.dto.request.RegisterRequest;
import com.agritech.auth.dto.response.AuthResponse;
import com.agritech.auth.dto.response.UserInfoResponse;
import com.agritech.auth.entity.RefreshToken;
import com.agritech.auth.entity.User;
import com.agritech.auth.event.producer.UserEventProducer;
import com.agritech.auth.repository.RefreshTokenRepository;
import com.agritech.auth.repository.UserRepository;
import com.agritech.auth.service.AuthService;
import com.agritech.common.constants.AppConstants;
import com.agritech.common.exception.BadRequestException;
import com.agritech.common.exception.DuplicateResourceException;
import com.agritech.common.exception.ResourceNotFoundException;
import com.agritech.common.exception.UnauthorizedException;
import com.agritech.common.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Implementation of {@link AuthService}.
 *
 * <p>Handles user registration, credential verification, JWT issuance,
 * refresh token rotation, and logout. Publishes Kafka events on user lifecycle
 * changes so downstream services (e.g., User Service) can react accordingly.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserEventProducer userEventProducer;

    @Value("${jwt.secret}")
    private String jwtSecret;

    /**
     * {@inheritDoc}
     *
     * <p>Validates uniqueness of email, persists the user with an encoded password,
     * publishes a {@code UserRegisteredEvent} to Kafka, and returns a token pair.
     */
    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Registering user with email: {}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("User", "email", request.getEmail());
        }

        User user = User.builder()
                .email(request.getEmail().toLowerCase().trim())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .active(true)
                .verified(false)
                .build();

        user = userRepository.save(user);
        log.info("User registered successfully with ID: {}", user.getId());

        // Publish event to User Service via Kafka so it can create the full profile
        userEventProducer.publishUserRegistered(user, request.getFirstName(), request.getLastName(), request.getPhone());

        String accessToken = jwtUtil.generateAccessToken(
                user.getEmail(), user.getId(), user.getRole().name(), user.isVerified());
        RefreshToken refreshToken = createRefreshToken(user);

        return buildAuthResponse(user, accessToken, refreshToken.getToken());
    }

    /**
     * {@inheritDoc}
     *
     * <p>Looks up the user by email, validates the account is active,
     * verifies the password, and issues a new token pair.
     */
    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        log.info("Login attempt for email: {}", request.getEmail());

        User user = userRepository.findByEmail(request.getEmail().toLowerCase().trim())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (!user.isActive()) {
            throw new UnauthorizedException("Account is deactivated. Please contact support.");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("Invalid password attempt for email: {}", request.getEmail());
            throw new UnauthorizedException("Invalid email or password");
        }

        String accessToken = jwtUtil.generateAccessToken(
                user.getEmail(), user.getId(), user.getRole().name(), user.isVerified());
        RefreshToken refreshToken = createRefreshToken(user);

        log.info("Login successful for user ID: {}", user.getId());
        return buildAuthResponse(user, accessToken, refreshToken.getToken());
    }

    /**
     * {@inheritDoc}
     *
     * <p>Validates the provided refresh token, checks it is not revoked or expired,
     * issues a new access token, and rotates the refresh token (old one is revoked).
     */
    @Override
    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        if (refreshToken.isRevoked()) {
            throw new UnauthorizedException("Refresh token has been revoked");
        }

        if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new UnauthorizedException("Refresh token has expired. Please login again.");
        }

        User user = refreshToken.getUser();
        String newAccessToken = jwtUtil.generateAccessToken(
                user.getEmail(), user.getId(), user.getRole().name(), user.isVerified());
        RefreshToken newRefreshToken = createRefreshToken(user);

        // Revoke old token — token rotation for security
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);

        log.info("Refresh token rotated for user ID: {}", user.getId());
        return buildAuthResponse(user, newAccessToken, newRefreshToken.getToken());
    }

    /**
     * {@inheritDoc}
     *
     * <p>Marks the refresh token as revoked. The corresponding access token
     * remains valid until it naturally expires (stateless JWT).
     */
    @Override
    @Transactional
    public void logout(String refreshTokenValue) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenValue)
                .orElseThrow(() -> new BadRequestException("Refresh token not found"));
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);
        log.info("User logged out. Refresh token revoked for user ID: {}", refreshToken.getUser().getId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public UserInfoResponse getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        return UserInfoResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .active(user.isActive())
                .verified(user.isVerified())
                .createdAt(user.getCreatedAt())
                .build();
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    /**
     * Creates and persists a new refresh token for the given user.
     *
     * @param user the owner of the new refresh token
     * @return the persisted RefreshToken entity
     */
    private RefreshToken createRefreshToken(User user) {
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(LocalDateTime.now().plusSeconds(AppConstants.REFRESH_TOKEN_EXPIRY / 1000))
                .revoked(false)
                .build();
        return refreshTokenRepository.save(refreshToken);
    }

    /**
     * Builds the {@link AuthResponse} from a user, access token, and refresh token value.
     *
     * @param user              the authenticated user
     * @param accessToken       the generated JWT access token
     * @param refreshTokenValue the raw refresh token string
     * @return a fully populated AuthResponse
     */
    private AuthResponse buildAuthResponse(User user, String accessToken, String refreshTokenValue) {
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenValue)
                .tokenType("Bearer")
                .expiresIn(AppConstants.ACCESS_TOKEN_EXPIRY / 1000)
                .userId(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .verified(user.isVerified())
                .build();
    }
}
