package com.example.secure_customer_api.controller;

import com.example.secure_customer_api.dto.UpdateProfileDTO;
import com.example.secure_customer_api.dto.UserResponseDTO;
import com.example.secure_customer_api.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

  @Autowired
  private UserService userService;

  // ==================== Exercise 7: User Profile Management ====================

  /**
   * Task 7.1: View Profile (3 points)
   * GET /api/users/profile - View current user's profile
   */
  @GetMapping("/profile")
  public ResponseEntity<UserResponseDTO> getProfile() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String username = authentication.getName();

    UserResponseDTO user = userService.getCurrentUser(username);
    return ResponseEntity.ok(user);
  }

  /**
   * Task 7.2: Update Profile (4 points)
   * PUT /api/users/profile - Update user's full name and email
   */
  @PutMapping("/profile")
  public ResponseEntity<UserResponseDTO> updateProfile(@Valid @RequestBody UpdateProfileDTO updateProfileDTO) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String username = authentication.getName();

    UserResponseDTO updatedUser = userService.updateProfile(username, updateProfileDTO);
    return ResponseEntity.ok(updatedUser);
  }

  /**
   * Task 7.3: Delete Account (3 points)
   * DELETE /api/users/account - Soft delete user account (requires password
   * verification)
   */
  @DeleteMapping("/account")
  public ResponseEntity<Map<String, String>> deleteAccount(@RequestParam String password) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String username = authentication.getName();

    Map<String, String> response = userService.deleteAccount(username, password);
    return ResponseEntity.ok(response);
  }
}
