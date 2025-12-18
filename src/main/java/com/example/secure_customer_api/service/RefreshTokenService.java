package com.example.secure_customer_api.service;

import com.example.secure_customer_api.entity.RefreshToken;
import com.example.secure_customer_api.entity.User;
import com.example.secure_customer_api.exception.ResourceNotFoundException;
import com.example.secure_customer_api.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class RefreshTokenService {

  @Value("${jwt.refresh-expiration:604800000}") // Default 7 days in milliseconds
  private long refreshTokenExpiration;

  @Autowired
  private RefreshTokenRepository refreshTokenRepository;

  public RefreshToken createRefreshToken(User user) {
    // Delete existing refresh token for user if exists
    refreshTokenRepository.findByUser(user).ifPresent(token -> refreshTokenRepository.delete(token));

    // Create new refresh token
    RefreshToken refreshToken = new RefreshToken();
    refreshToken.setUser(user);
    refreshToken.setToken(UUID.randomUUID().toString());
    refreshToken.setExpiryDate(LocalDateTime.now().plusSeconds(refreshTokenExpiration / 1000));

    return refreshTokenRepository.save(refreshToken);
  }

  public Optional<RefreshToken> findByToken(String token) {
    return refreshTokenRepository.findByToken(token);
  }

  public RefreshToken verifyExpiration(RefreshToken token) {
    if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
      refreshTokenRepository.delete(token);
      throw new ResourceNotFoundException("Refresh token has expired. Please login again.");
    }
    return token;
  }

  public void deleteByUser(User user) {
    refreshTokenRepository.deleteByUser(user);
  }
}
