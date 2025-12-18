package com.example.secure_customer_api.controller;

import com.example.secure_customer_api.dto.*;
import com.example.secure_customer_api.entity.RefreshToken;
import com.example.secure_customer_api.exception.ResourceNotFoundException;
import com.example.secure_customer_api.security.JwtTokenProvider;
import com.example.secure_customer_api.service.RefreshTokenService;
import com.example.secure_customer_api.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired(required = false)
    private RefreshTokenService refreshTokenService;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        LoginResponseDTO response = userService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody RegisterRequestDTO registerRequest) {
        UserResponseDTO response = userService.register(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        UserResponseDTO user = userService.getCurrentUser(username);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout() {
        // In JWT, logout is handled client-side by removing token
        Map<String, String> response = new HashMap<>();
        response.put("message", "Logged out successfully. Please remove token from client.");
        return ResponseEntity.ok(response);
    }

    // ==================== Exercise 6: Password Management ====================

    @PutMapping("/change-password")
    public ResponseEntity<Map<String, String>> changePassword(@Valid @RequestBody ChangePasswordDTO changePasswordDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Map<String, String> response = userService.changePassword(username, changePasswordDTO);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@Valid @RequestBody ForgotPasswordDTO forgotPasswordDTO) {
        Map<String, String> response = userService.forgotPassword(forgotPasswordDTO);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@Valid @RequestBody ResetPasswordDTO resetPasswordDTO) {
        Map<String, String> response = userService.resetPassword(resetPasswordDTO);
        return ResponseEntity.ok(response);
    }

    // ==================== Exercise 9: Refresh Token ====================

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDTO> refreshToken(@Valid @RequestBody RefreshTokenDTO refreshTokenDTO) {
        if (refreshTokenService == null) {
            throw new ResourceNotFoundException("Refresh token service not available");
        }

        return refreshTokenService.findByToken(refreshTokenDTO.getRefreshToken())
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    // Create authentication for token generation
                    Authentication authentication = new UsernamePasswordAuthenticationToken(
                            user.getUsername(),
                            null,
                            Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())));

                    String newAccessToken = tokenProvider.generateToken(authentication);

                    // Optionally generate new refresh token
                    String newRefreshToken = refreshTokenService.createRefreshToken(user).getToken();

                    return ResponseEntity.ok(new LoginResponseDTO(
                            newAccessToken,
                            newRefreshToken,
                            user.getUsername(),
                            user.getEmail(),
                            user.getRole().name()));
                })
                .orElseThrow(() -> new ResourceNotFoundException("Refresh token not found"));
    }
}
