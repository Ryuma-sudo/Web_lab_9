package com.example.secure_customer_api.controller;

import com.example.secure_customer_api.dto.UpdateRoleDTO;
import com.example.secure_customer_api.dto.UserResponseDTO;
import com.example.secure_customer_api.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

  @Autowired
  private UserService userService;

  // ==================== Exercise 8: Admin Endpoints ====================

  /**
   * Task 8.1: List All Users (3 points)
   * GET /api/admin/users - Return all users (admin only)
   */
  @GetMapping("/users")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
    List<UserResponseDTO> users = userService.getAllUsers();
    return ResponseEntity.ok(users);
  }

  /**
   * Task 8.2: Update User Role (4 points)
   * PUT /api/admin/users/{id}/role - Update user's role
   */
  @PutMapping("/users/{id}/role")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<UserResponseDTO> updateUserRole(
      @PathVariable Long id,
      @Valid @RequestBody UpdateRoleDTO updateRoleDTO) {
    UserResponseDTO updatedUser = userService.updateUserRole(id, updateRoleDTO);
    return ResponseEntity.ok(updatedUser);
  }

  /**
   * Task 8.3: Deactivate/Activate User (3 points)
   * PATCH /api/admin/users/{id}/status - Toggle user's isActive status
   */
  @PatchMapping("/users/{id}/status")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<UserResponseDTO> toggleUserStatus(@PathVariable Long id) {
    UserResponseDTO updatedUser = userService.toggleUserStatus(id);
    return ResponseEntity.ok(updatedUser);
  }
}
