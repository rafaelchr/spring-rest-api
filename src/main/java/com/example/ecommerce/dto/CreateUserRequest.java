package com.example.ecommerce.dto;

import com.example.ecommerce.exception.BadRequestException;
import com.example.ecommerce.model.Role;
import com.example.ecommerce.model.User;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequest {
  @NotBlank(message = "Username tidak boleh kosong")
  private String username;

  @NotBlank(message = "Email tidak boleh kosong")
  private String email;

  @NotBlank(message = "Password tidak boleh kosong")
  private String password;

  @NotBlank(message = "Role tidak boleh kosong")
  private String role;

  public Role getRoleEnum() {
    try {
      return Role.valueOf(role.toUpperCase());
    } catch (IllegalArgumentException ex) {
      throw new BadRequestException("Role tidak valid: " + role);
    }
  }

  public User toEntity() {
    return User.builder()
        .username(this.username)
        .email(this.email)
        .password(this.password)
        .role(getRoleEnum())
        .build();
  }
}
