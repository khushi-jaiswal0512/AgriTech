package com.agritech.auth.scheduler;

import com.agritech.auth.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Scheduled job that cleans up expired and revoked refresh tokens.
 *
 * <p>Runs every day at midnight (server local time) to keep the
 * {@code refresh_tokens} table lean. Without cleanup, rows accumulate
 * indefinitely because revoked tokens are only soft-deleted during logout.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TokenCleanupScheduler {

    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * Deletes all refresh tokens that are either expired or revoked.
     *
     * <p>Scheduled expression: {@code "0 0 0 * * ?"} — every day at midnight.
     * The operation is wrapped in a transaction so a partial failure does not
     * leave the table in an inconsistent state.
     */
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void cleanupExpiredTokens() {
        log.info("Starting scheduled refresh token cleanup...");
        int deleted = refreshTokenRepository.deleteExpiredAndRevoked(LocalDateTime.now());
        log.info("Token cleanup complete: removed {} expired/revoked refresh token(s)", deleted);
    }
}
