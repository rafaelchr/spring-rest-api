package com.example.ecommerce.seeder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.ecommerce.model.Role;
import com.example.ecommerce.model.User;
import com.example.ecommerce.repository.UserRepository;

@Component
public class UserSeeder implements CommandLineRunner {
  @Autowired
  private UserRepository userRepository;

  @Override
  public void run(String... args) throws Exception {
    if (userRepository.count() == 0) {
      BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
      User user = new User();
      user.setUsername("admin");
      user.setEmail("admin@example.com");
      user.setPassword(encoder.encode("admin123"));
      user.setRole(Role.ADMIN);

      userRepository.save(user);
      System.out.println("Seeder: Admin user created.");
    } else {
      System.out.println("Seeder: Users already exist, skipping.");
    }
  }
}
