package com.example.secure_customer_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ResetPasswordDTO {

  @NotBlank(message = "Reset token is required")
  private String token;

  @NotBlank(message = "New password is required")
  @Size(min = 6, message = "Password must be at least 6 characters")
  private String newPassword;

  @NotBlank(message = "Confirm password is required")
  private String confirmPassword;

  // Constructors
  public ResetPasswordDTO() {
  }

  public ResetPasswordDTO(String token, String newPassword, String confirmPassword) {
    this.token = token;
    this.newPassword = newPassword;
    this.confirmPassword = confirmPassword;
  }

  // Getters and Setters
  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public String getNewPassword() {
    return newPassword;
  }

  public void setNewPassword(String newPassword) {
    this.newPassword = newPassword;
  }

  public String getConfirmPassword() {
    return confirmPassword;
  }

  public void setConfirmPassword(String confirmPassword) {
    this.confirmPassword = confirmPassword;
  }
}
