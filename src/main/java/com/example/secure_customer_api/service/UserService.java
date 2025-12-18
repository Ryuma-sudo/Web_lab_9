package com.example.secure_customer_api.service;

import com.example.secure_customer_api.dto.*;

import java.util.List;
import java.util.Map;

public interface UserService {

    // Authentication
    LoginResponseDTO login(LoginRequestDTO loginRequest);

    UserResponseDTO register(RegisterRequestDTO registerRequest);

    UserResponseDTO getCurrentUser(String username);

    // Exercise 6: Password Management
    Map<String, String> changePassword(String username, ChangePasswordDTO changePasswordDTO);

    Map<String, String> forgotPassword(ForgotPasswordDTO forgotPasswordDTO);

    Map<String, String> resetPassword(ResetPasswordDTO resetPasswordDTO);

    // Exercise 7: User Profile Management
    UserResponseDTO updateProfile(String username, UpdateProfileDTO updateProfileDTO);

    Map<String, String> deleteAccount(String username, String password);

    // Exercise 8: Admin Endpoints
    List<UserResponseDTO> getAllUsers();

    UserResponseDTO updateUserRole(Long id, UpdateRoleDTO updateRoleDTO);

    UserResponseDTO toggleUserStatus(Long id);
}
