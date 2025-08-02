package com.example.ecommerce.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ecommerce.dto.CreateUserRequest;
import com.example.ecommerce.dto.PagedResponse;
import com.example.ecommerce.dto.UpdateUserRequest;
import com.example.ecommerce.dto.UserResponse;
import com.example.ecommerce.exception.BadRequestException;
import com.example.ecommerce.exception.ConflictException;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.model.Role;
import com.example.ecommerce.model.User;
import com.example.ecommerce.service.UserService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/users")
public class UserController {
  @Autowired
  private UserService userService;

  @GetMapping
  @PreAuthorize("hasAuthority('ADMIN')")
  public PagedResponse<UserResponse> getAllUsers(Pageable pageable) {
    Page<UserResponse> page = userService.findAll(pageable);
    return new PagedResponse<>(
        page.getContent(),
        page.getNumber(),
        page.getSize(),
        page.getTotalElements(),
        page.getTotalPages());
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasAuthority('ADMIN')")
  public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
    return userService.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
  }

  @PostMapping
  @PreAuthorize("hasAuthority('ADMIN')")
  public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
    if (userService.existByUsername(request.getUsername())) {
      throw new ConflictException("Username already taken");
    }

    if (userService.existsByEmail(request.getEmail())) {
      throw new ConflictException("Email already taken");
    }

    return ResponseEntity.ok(userService.save(request.toEntity()));
  }

  @PatchMapping("/{id}")
  @PreAuthorize("hasAuthority('ADMIN')")
  public ResponseEntity<UserResponse> updateUser(
      @PathVariable Long id,
      @Valid @RequestBody UpdateUserRequest request) {

    User user = userService.findByIdEntity(id)
        .orElseThrow(() -> new ResourceNotFoundException("User with ID " + id + " not found"));

    if (request.getUsername() != null) {
      if (request.getUsername().isBlank()) {
        throw new BadRequestException("Username can't be blank");
      }

      if (!request.getUsername().equals(user.getUsername())) {
        boolean usernameTaken = userService.existByUsername(request.getUsername());
        if (usernameTaken) {
          throw new ConflictException("Username already taken");
        }
      }
      user.setUsername(request.getUsername());
    }

    if (request.getEmail() != null) {
      if (request.getEmail().isBlank()) {
        throw new BadRequestException("Email can't be blank");
      }

      if (!request.getEmail().equals(user.getEmail())) {
        boolean emailTaken = userService.existsByEmail(request.getEmail());
        if (emailTaken) {
          throw new ConflictException("Email already taken");
        }
      }
      user.setEmail(request.getEmail());
    }

    if (request.getPassword() != null) {
      if (request.getPassword().isBlank()) {
        throw new BadRequestException("Password can't be blank");
      }
      user.setPassword(request.getPassword());
    }

    if (request.getRole() != null) {
      try {
        Role newRole = Role.valueOf(request.getRole().toUpperCase());
        user.setRole(newRole);
      } catch (IllegalArgumentException ex) {
        throw new BadRequestException("Invalid Role: " + request.getRole());
      }
    }

    userService.save(user);

    UserResponse response = new UserResponse(user.getId(), user.getUsername(), user.getEmail(), user.getRole());
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthority('ADMIN')")
  public ResponseEntity<Boolean> deleteUser(@PathVariable Long id) {
    return userService.findById(id).map(product -> {
      userService.deleteById(id);
      return ResponseEntity.ok(true);
    }).orElseThrow(() -> new ResourceNotFoundException("User with ID " + id + " not found"));
  }

  @GetMapping("/me")
  public ResponseEntity<UserResponse> getCurrentUser(Authentication authentication) {
    return ResponseEntity.ok(userService.getCurrentUser(authentication));
  }

  @PatchMapping("/me")
  public ResponseEntity<UserResponse> updateCurrentUser(
      @RequestBody UpdateUserRequest request,
      Authentication authentication) {

    User user = userService.getCurrentUserEntity(authentication);

    if (request.getUsername() != null) {
      if (request.getUsername().isBlank()) {
        throw new BadRequestException("Username can't be blank");
      }

      if (!request.getUsername().equals(user.getUsername())) {
        boolean usernameTaken = userService.existByUsername(request.getUsername());
        if (usernameTaken) {
          throw new ConflictException("Username already taken");
        }
      }
      user.setUsername(request.getUsername());
    }

    if (request.getEmail() != null) {
      if (request.getEmail().isBlank()) {
        throw new BadRequestException("Email can't be blank");
      }

      if (!request.getEmail().equals(user.getEmail())) {
        boolean emailTaken = userService.existsByEmail(request.getEmail());
        if (emailTaken) {
          throw new ConflictException("Email already taken");
        }
      }
      user.setEmail(request.getEmail());
    }

    if (request.getPassword() != null) {
      if (request.getPassword().isBlank()) {
        throw new BadRequestException("Password can't be blank");
      }
      user.setPassword(request.getPassword());
    }

    userService.save(user);

    UserResponse response = new UserResponse(user.getId(), user.getUsername(), user.getEmail(), user.getRole());
    return ResponseEntity.ok(response);
  }
}
