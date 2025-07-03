package com.prajwal.securevault.controller;

import com.prajwal.securevault.dto.UsernameRecoveryRequest;
import com.prajwal.securevault.entity.User;
import com.prajwal.securevault.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class UsernameRecoveryController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/recover-username")
    public String showRecoverUsernamePage(Model model) {
        model.addAttribute("usernameRecoveryRequest", new UsernameRecoveryRequest());
        return "recover-username-by-phone";
    }

    @PostMapping("/recover-username")
    public String processRecoverUsername(
            @Valid @ModelAttribute("usernameRecoveryRequest") UsernameRecoveryRequest request,
            BindingResult result,
            Model model) {

        if (result.hasErrors()) {
            return "recover-username-by-phone";
        }

        Optional<User> userOpt = userRepository.findByPhoneNumber(request.getPhoneNumber());

        if (userOpt.isPresent()) {
            model.addAttribute("username", userOpt.get().getUsername());
        } else {
            model.addAttribute("message", "❌ No account found with this phone number");
        }

        return "recover-username-by-phone";
    }
}
