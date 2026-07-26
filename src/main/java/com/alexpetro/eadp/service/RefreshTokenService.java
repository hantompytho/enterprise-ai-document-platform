package com.alexpetro.eadp.service;

import com.alexpetro.eadp.entity.RefreshToken;
import com.alexpetro.eadp.entity.User;
import com.alexpetro.eadp.repository.RefreshTokenRepository;
import com.alexpetro.eadp.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.alexpetro.eadp.exception.InvalidRefreshTokenException;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private static final long REFRESH_TOKEN_VALIDITY_DAYS = 7;

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    public RefreshTokenService(
            RefreshTokenRepository refreshTokenRepository,
            UserRepository userRepository
    ) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public RefreshToken createRefreshToken(String email) {
        User user = userRepository
                .findByEmailIgnoreCase(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found")
                );

        refreshTokenRepository
                .findByUser(user)
                .ifPresent(existingToken -> {
                    refreshTokenRepository.delete(existingToken);
                    refreshTokenRepository.flush();
                });

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setUser(user);
        refreshToken.setExpiresAt(
                LocalDateTime.now()
                        .plusDays(REFRESH_TOKEN_VALIDITY_DAYS)
        );

        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken verifyRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository
                .findByToken(token)
                .orElseThrow(() ->
                        new InvalidRefreshTokenException(
                                "Refresh token not found"
                        )
                );

        if (refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(refreshToken);

            throw new InvalidRefreshTokenException("Refresh token not found");
        }

        return refreshToken;
    }

    @Transactional
    public void deleteRefreshToken(String token) {
        refreshTokenRepository
                .findByToken(token)
                .ifPresent(refreshTokenRepository::delete);
    }

    @Transactional
    public void deleteRefreshTokenByUser(String email) {
        User user = userRepository
                .findByEmailIgnoreCase(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found")
                );

        refreshTokenRepository.deleteByUser(user);
    }

    @Transactional
    public RefreshToken rotateRefreshToken(String oldTokenValue) {

        RefreshToken oldToken = refreshTokenRepository
                .findByToken(oldTokenValue)
                .orElseThrow(() ->
                        new InvalidRefreshTokenException(
                                "Refresh token not found"
                        )
                );

        if (oldToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(oldToken);
            refreshTokenRepository.flush();

            throw new InvalidRefreshTokenException(
                    "Refresh token has expired"
            );
        }

        User user = oldToken.getUser();

        refreshTokenRepository.delete(oldToken);
        refreshTokenRepository.flush();

        RefreshToken newToken = new RefreshToken();
        newToken.setToken(UUID.randomUUID().toString());
        newToken.setUser(user);
        newToken.setExpiresAt(
                LocalDateTime.now()
                        .plusDays(REFRESH_TOKEN_VALIDITY_DAYS)
        );

        return refreshTokenRepository.save(newToken);
    }
}