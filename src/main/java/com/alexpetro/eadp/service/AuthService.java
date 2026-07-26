package com.alexpetro.eadp.service;

import com.alexpetro.eadp.dto.RegisterRequest;
import com.alexpetro.eadp.dto.UserResponse;
import com.alexpetro.eadp.entity.RefreshToken;
import com.alexpetro.eadp.entity.Role;
import com.alexpetro.eadp.entity.User;
import com.alexpetro.eadp.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.alexpetro.eadp.exception.EmailAlreadyExistsException;
import com.alexpetro.eadp.dto.LoginRequest;
import com.alexpetro.eadp.dto.LoginResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            CustomUserDetailsService customUserDetailsService,
            JwtService jwtService,
            RefreshTokenService refreshTokenService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.customUserDetailsService = customUserDetailsService;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    public UserResponse register(RegisterRequest request) {
        String normalizedEmail = request.getEmail()
                .trim()
                .toLowerCase();

        if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new EmailAlreadyExistsException(normalizedEmail);
        }

        User user = new User();
        user.setEmail(normalizedEmail);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);

        User savedUser = userRepository.save(user);

        return new UserResponse(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getRole(),
                savedUser.getCreatedAt()
        );
    }

    public LoginResponse login(LoginRequest request) {
        String normalizedEmail = request.getEmail()
                .trim()
                .toLowerCase();

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        normalizedEmail,
                        request.getPassword()
                )
        );

        var userDetails =
                customUserDetailsService.loadUserByUsername(normalizedEmail);

        String accessToken = jwtService.generateToken(userDetails);

        RefreshToken refreshToken =
                refreshTokenService.createRefreshToken(normalizedEmail);

        return new LoginResponse(
                accessToken,
                refreshToken.getToken(),
                "Bearer",
                3600
        );
    }

    public LoginResponse refreshAccessToken(String refreshTokenValue) {

        RefreshToken newRefreshToken =
                refreshTokenService.rotateRefreshToken(
                        refreshTokenValue
                );

        String email = newRefreshToken
                .getUser()
                .getEmail();

        var userDetails =
                customUserDetailsService.loadUserByUsername(email);

        String newAccessToken =
                jwtService.generateToken(userDetails);

        return new LoginResponse(
                newAccessToken,
                newRefreshToken.getToken(),
                "Bearer",
                3600
        );
    }

    public void logout(String refreshTokenValue) {
        refreshTokenService.deleteRefreshToken(refreshTokenValue);
    }

    public UserResponse getCurrentUser(String email) {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found")
                );

        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt()
        );
    }
}