package com.example.secure_customer_api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class UpdateProfileDTO {

  @NotBlank(message = "Full name is required")
  private String fullName;

  @Email(message = "Email must be valid")
  private String email;

  // Constructors
  public UpdateProfileDTO() {
  }

  public UpdateProfileDTO(String fullName, String email) {
    this.fullName = fullName;
    this.email = email;
  }

  // Getters and Setters
  public String getFullName() {
    return fullName;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }
}
