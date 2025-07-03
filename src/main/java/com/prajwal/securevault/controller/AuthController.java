package com.prajwal.securevault.controller;

import com.prajwal.securevault.dto.RegisterRequest;
import com.prajwal.securevault.entity.User;
import com.prajwal.securevault.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import com.prajwal.securevault.dto.ForgotPasswordPhoneRequest;

@Controller
@RequestMapping
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // LOGIN PAGE
    @GetMapping("/login")
    public String showLoginPage(@RequestParam(value = "error", required = false) String error,
                                @RequestParam(value = "logout", required = false) String logout,
                                @RequestParam(value = "message", required = false) String message,
                                Model model) {

        if (error != null) {
            model.addAttribute("message", "Invalid username or password.");
        }
        if (logout != null) {
            model.addAttribute("message", "You have been logged out successfully.");
        }
        if (message != null) {
            model.addAttribute("message", message);
        }

        return "login";
    }

    // REGISTER PAGE
    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "register";
    }

    // PROCESS REGISTRATION
    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("registerRequest") RegisterRequest registerRequest,
                           BindingResult result,
                           Model model) {

        // 🔍 1. If validation errors exist (from DTO)
        if (result.hasErrors()) {
            return "register";
        }

        // 🔍 2. Check if username is already taken
        if (userRepository.findByUsername(registerRequest.getUsername()).isPresent()) {
            model.addAttribute("message", "Username already exists!");
            return "register";
        }

        // 🔍 3. Check if email already used
        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            model.addAttribute("message", "Email already in use!");
            return "register";
        }

        // 🔍 4. Optional: You can also check if phone number is taken (add if needed)
        // if (userRepository.findByPhoneNumber(registerRequest.getPhoneNumber()).isPresent()) {
        //     model.addAttribute("message", "Phone number already in use!");
        //     return "register";
        // }

        // ✅ 5. Create new user and save
        User newUser = new User();
        newUser.setUsername(registerRequest.getUsername());
        newUser.setEmail(registerRequest.getEmail());
        newUser.setPhoneNumber(registerRequest.getPhoneNumber());
        newUser.setPassword(passwordEncoder.encode(registerRequest.getPassword())); // Always encode

        userRepository.save(newUser);

        // ✅ 6. Redirect to login page with message
        return "redirect:/login?message=Registration successful! Please login.";
    }


}