package com.prajwal.securevault.controller;

import com.prajwal.securevault.dto.PhoneRecoveryRequest;
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
public class EmailRecoveryController {

    @Autowired
    private UserRepository userRepository;

    // ✅ Step 1: Show the recover email page
    @GetMapping("/recover-email")
    public String showRecoverEmailPage(Model model) {
        model.addAttribute("phoneRecoveryRequest", new PhoneRecoveryRequest());
        return "recover-email-by-phone";  // ✔️ Your HTML filename
    }

    // ✅ Step 2: Process phone and return email if found
    @PostMapping("/recover-email")
    public String processRecoverEmail(
            @Valid @ModelAttribute("phoneRecoveryRequest") PhoneRecoveryRequest request,
            BindingResult result,
            Model model) {

        if (result.hasErrors()) {
            return "recover-email-by-phone"; // returns form with validation errors
        }

        Optional<User> userOpt = userRepository.findByPhoneNumber(request.getPhoneNumber());

        if (userOpt.isPresent()) {
            model.addAttribute("email", userOpt.get().getEmail());
        } else {
            model.addAttribute("message", "❌ No account found with this phone number");
        }

        return "recover-email-by-phone";
    }
}