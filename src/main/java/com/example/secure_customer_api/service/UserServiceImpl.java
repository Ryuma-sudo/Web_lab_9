package com.example.secure_customer_api.service;

import com.example.secure_customer_api.dto.*;
import com.example.secure_customer_api.entity.Role;
import com.example.secure_customer_api.entity.User;
import com.example.secure_customer_api.exception.DuplicateResourceException;
import com.example.secure_customer_api.exception.ResourceNotFoundException;
import com.example.secure_customer_api.repository.UserRepository;
import com.example.secure_customer_api.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired(required = false)
    private RefreshTokenService refreshTokenService;

    // ==================== Authentication ====================

    @Override
    public LoginResponseDTO login(LoginRequestDTO loginRequest) {
        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Generate JWT token
        String token = tokenProvider.generateToken(authentication);

        // Get user details
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Generate refresh token if service is available
        String refreshToken = null;
        if (refreshTokenService != null) {
            refreshToken = refreshTokenService.createRefreshToken(user).getToken();
        }

        return new LoginResponseDTO(
                token,
                refreshToken,
                user.getUsername(),
                user.getEmail(),
                user.getRole().name());
    }

    @Override
    public UserResponseDTO register(RegisterRequestDTO registerRequest) {
        // Check if username exists
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new DuplicateResourceException("Username already exists");
        }

        // Check if email exists
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }

        // Create new user
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setFullName(registerRequest.getFullName());
        user.setRole(Role.USER); // Default role
        user.setIsActive(true);

        User savedUser = userRepository.save(user);

        return convertToDTO(savedUser);
    }

    @Override
    public UserResponseDTO getCurrentUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return convertToDTO(user);
    }

    // ==================== Exercise 6: Password Management ====================

    @Override
    public Map<String, String> changePassword(String username, ChangePasswordDTO changePasswordDTO) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Verify current password
        if (!passwordEncoder.matches(changePasswordDTO.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        // Check if new password matches confirm password
        if (!changePasswordDTO.getNewPassword().equals(changePasswordDTO.getConfirmPassword())) {
            throw new IllegalArgumentException("New password and confirm password do not match");
        }

        // Hash and update password
        user.setPassword(passwordEncoder.encode(changePasswordDTO.getNewPassword()));
        userRepository.save(user);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Password changed successfully");
        return response;
    }

    @Override
    public Map<String, String> forgotPassword(ForgotPasswordDTO forgotPasswordDTO) {
        User user = userRepository.findByEmail(forgotPasswordDTO.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User with this email not found"));

        // Generate reset token
        String resetToken = UUID.randomUUID().toString();

        // Set token and expiry (1 hour)
        user.setResetToken(resetToken);
        user.setResetTokenExpiry(LocalDateTime.now().plusHours(1));
        userRepository.save(user);

        // In real application, this token would be sent via email
        Map<String, String> response = new HashMap<>();
        response.put("message", "Password reset token generated. In production, this would be sent via email.");
        response.put("resetToken", resetToken); // Only for testing; remove in production
        return response;
    }

    @Override
    public Map<String, String> resetPassword(ResetPasswordDTO resetPasswordDTO) {
        User user = userRepository.findAll().stream()
                .filter(u -> resetPasswordDTO.getToken().equals(u.getResetToken()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Invalid reset token"));

        // Check if token is expired
        if (user.getResetTokenExpiry() == null || user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Reset token has expired");
        }

        // Check if new password matches confirm password
        if (!resetPasswordDTO.getNewPassword().equals(resetPasswordDTO.getConfirmPassword())) {
            throw new IllegalArgumentException("New password and confirm password do not match");
        }

        // Update password and clear reset token
        user.setPassword(passwordEncoder.encode(resetPasswordDTO.getNewPassword()));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Password reset successfully");
        return response;
    }

    // ==================== Exercise 7: User Profile Management ====================

    @Override
    public UserResponseDTO updateProfile(String username, UpdateProfileDTO updateProfileDTO) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Check if new email already exists (and is different from current)
        if (updateProfileDTO.getEmail() != null &&
                !updateProfileDTO.getEmail().equals(user.getEmail()) &&
                userRepository.existsByEmail(updateProfileDTO.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }

        // Update profile
        if (updateProfileDTO.getFullName() != null) {
            user.setFullName(updateProfileDTO.getFullName());
        }
        if (updateProfileDTO.getEmail() != null) {
            user.setEmail(updateProfileDTO.getEmail());
        }

        User updatedUser = userRepository.save(user);
        return convertToDTO(updatedUser);
    }

    @Override
    public Map<String, String> deleteAccount(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Verify password
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Password is incorrect");
        }

        // Soft delete - set isActive to false
        user.setIsActive(false);
        userRepository.save(user);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Account deleted successfully");
        return response;
    }

    // ==================== Exercise 8: Admin Endpoints ====================

    @Override
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponseDTO updateUserRole(Long id, UpdateRoleDTO updateRoleDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        user.setRole(updateRoleDTO.getRole());
        User updatedUser = userRepository.save(user);

        return convertToDTO(updatedUser);
    }

    @Override
    public UserResponseDTO toggleUserStatus(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        // Toggle isActive status
        user.setIsActive(!user.getIsActive());
        User updatedUser = userRepository.save(user);

        return convertToDTO(updatedUser);
    }

    // ==================== Helper Methods ====================

    private UserResponseDTO convertToDTO(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFullName(),
                user.getRole().name(),
                user.getIsActive(),
                user.getCreatedAt());
    }
}
