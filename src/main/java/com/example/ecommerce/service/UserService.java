package com.example.ecommerce.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.ecommerce.dto.UserResponse;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.model.User;
import com.example.ecommerce.repository.UserRepository;

@Service
public class UserService {

  private final PasswordEncoder passwordEncoder;
  @Autowired
  private UserRepository userRepository;

  UserService(PasswordEncoder passwordEncoder) {
    this.passwordEncoder = passwordEncoder;
  }

  public Page<UserResponse> findAll(Pageable pageable) {
    return userRepository.findAll(pageable)
        .map(user -> new UserResponse(
            user.getId(), user.getUsername(), user.getEmail(), user.getRole()));
  }

  public Optional<User> findByIdEntity(Long id) {
    return userRepository.findById(id);
  }

  public Optional<UserResponse> findById(Long id) {
    return userRepository.findById(id)
        .map(user -> new UserResponse(
            user.getId(), user.getUsername(), user.getEmail(), user.getRole()));
  }

  public UserResponse save(User user) {
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    User savedUser = userRepository.save(user);

    return new UserResponse(
        savedUser.getId(), savedUser.getUsername(), savedUser.getEmail(), savedUser.getRole());
  }

  public void deleteById(Long id) {
    userRepository.deleteById(id);
  }

  public boolean existById(Long id) {
    return userRepository.existsById(id);
  }

  public boolean existByUsername(String username) {
    return userRepository.existsByUsername(username);
  }

  public boolean existsByEmail(String email) {
    return userRepository.existsByEmail(email);
  }

  public UserResponse getCurrentUser(Authentication authentication) {
    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    User user = userRepository.findByUsername(userDetails.getUsername())
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    return new UserResponse(user.getId(), user.getUsername(), user.getEmail(), user.getRole());
  }

  public User getCurrentUserEntity(Authentication authentication) {
    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    return userRepository.findByUsername(userDetails.getUsername())
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));
  }
}
