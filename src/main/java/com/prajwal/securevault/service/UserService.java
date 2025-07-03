package com.prajwal.securevault.service;

import com.prajwal.securevault.entity.User;
import com.prajwal.securevault.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Check if user exists by username
    public boolean checkIfUserExists(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    // Update password by username (optional, keep if you want)
    public void updatePasswordByUsername(String username, String newPassword) {
        userRepository.findByUsername(username).ifPresent(user -> {
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
        });
    }

    // 🔐 Update password by email — This is needed for forgot password flow
    public void updatePasswordByEmail(String email, String newPassword) {
        userRepository.findByEmail(email).ifPresent(user -> {
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
        });
    }



    // Register a new user
    public void saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    // Find user by email (used in forgot password flow)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }


    // Get email by username
    public String getEmailByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(User::getEmail)
                .orElse(null);
    }
}
