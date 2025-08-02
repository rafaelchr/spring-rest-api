package com.example.ecommerce.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.AuthenticationException;

import com.example.ecommerce.dto.JwtResponse;
import com.example.ecommerce.dto.LoginRequest;
import com.example.ecommerce.dto.RegisterRequest;
import com.example.ecommerce.exception.ConflictException;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.exception.UnauthorizedException;
import com.example.ecommerce.model.Role;
import com.example.ecommerce.model.User;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.security.JwtUtils;

@Service
public class AuthService {
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private PasswordEncoder passwordEncoder;
  @Autowired
  private AuthenticationManager authManager;
  @Autowired
  private JwtUtils jwtUtils;

  public User register(RegisterRequest request) {
    if (userRepository.existsByUsername(request.getUsername())) {
      throw new ConflictException("Username already taken");
    }

    if (userRepository.existsByEmail(request.getEmail())) {
      throw new ConflictException("Email already taken");
    }

    User user = new User();
    user.setUsername(request.getUsername());
    user.setEmail(request.getEmail());
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    user.setRole(Role.CUSTOMER);

    return userRepository.save(user);
  }

  public JwtResponse login(LoginRequest request) {
    try {
      Authentication auth = authManager.authenticate(
          new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

      UserDetails userDetails = (UserDetails) auth.getPrincipal();
      User user = userRepository.findByUsername(userDetails.getUsername())
          .orElseThrow(() -> new ResourceNotFoundException("User not found"));

      String jwt = jwtUtils.generateToken(userDetails);

      return new JwtResponse(user.getUsername(), user.getEmail(), user.getRole().name(), jwt);
    } catch (AuthenticationException e) {
      throw new UnauthorizedException("Invalid username or password");
    }
  }
}
