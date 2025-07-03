//package com.prajwal.securevault.controller;
//
//import com.prajwal.securevault.dto.LoginRequest;
//import com.prajwal.securevault.entity.User;
//import com.prajwal.securevault.repository.UserRepository;
//
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.crypto.password.PasswordEncoder;
//@RestController
//public class UserController {
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    @PostMapping("/user-login")  // 👈 Changed path to avoid conflict
//    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
//        User user = userRepository.findByUsername(request.getUsername());
//
//        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
//        }
//
//        return ResponseEntity.ok("Login successful");
//    }
//}
