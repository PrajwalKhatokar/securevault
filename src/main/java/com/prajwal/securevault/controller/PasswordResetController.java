package com.prajwal.securevault.controller;

import com.prajwal.securevault.dto.ForgotPasswordPhoneRequest;
import com.prajwal.securevault.dto.ResetPasswordRequest;
import com.prajwal.securevault.dto.ForgotPasswordRequest;
import com.prajwal.securevault.dto.VerifyOtpRequest;
import com.prajwal.securevault.service.EmailService;
import com.prajwal.securevault.service.OtpService;
import com.prajwal.securevault.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import com.prajwal.securevault.repository.UserRepository;
import com.prajwal.securevault.entity.User;
import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;


//THIS FOR EMAIL RESET

@Controller
public class PasswordResetController {

    @Autowired
    private UserService userService;

    @Autowired
    private OtpService otpService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Autowired
    private UserRepository userRepository;


    // ✅ Step 1: Show forgot password form
    @GetMapping("/forgot-password")
    public String showForgotPasswordForm(Model model) {
        model.addAttribute("forgotPasswordRequest", new ForgotPasswordRequest());
        return "forgot-password";
    }


    // ✅ Step 2: Submit email and send OTP
    @PostMapping("/forgot-password")
    public String processForgotPassword(
            @Valid @ModelAttribute("forgotPasswordRequest") ForgotPasswordRequest forgotPasswordRequest,
            BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            return "forgot-password";
        }

        String email = forgotPasswordRequest.getEmail();

        if (userService.findByEmail(email).isEmpty()) {
            model.addAttribute("message", "❌ No user found with this email");
            return "forgot-password";
        }

        String otpCode = otpService.generateOtp(email);
        emailService.sendOtpEmail(email, otpCode);

        model.addAttribute("verifyOtpRequest", new VerifyOtpRequest());
        model.addAttribute("email", email); // ✅ Only once
        model.addAttribute("message", "✅ OTP sent to your email");
        return "verify-otp";
    }



    // ✅ Step 3: Show OTP verification form
    @GetMapping("/verify-otp")
    public String showVerifyOtpForm(@RequestParam("email") String email, Model model) {
        model.addAttribute("verifyOtpRequest", new VerifyOtpRequest());
        model.addAttribute("email", email);
        return "verify-otp";
    }





    // ✅ Step 4: Process OTP
    @PostMapping("/verify-otp")
    public String processVerifyOtp(@Valid @ModelAttribute VerifyOtpRequest verifyOtpRequest,
                                   BindingResult bindingResult,
                                   @RequestParam("email") String email,
                                   Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("email", email);
            return "verify-otp";
        }

        boolean isValid = otpService.validateOtp(email, verifyOtpRequest.getOtpCode());

        if (!isValid) {
            model.addAttribute("message", "❌ Invalid or expired OTP");
            model.addAttribute("email", email);  // ✅ maintain for form
            return "verify-otp";
        }

        model.addAttribute("resetPasswordRequest", new ResetPasswordRequest());
        model.addAttribute("email", email);
        return "reset-password";
    }

    // ✅ Step 5: Show new password form (optional direct GET)
    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam("email") String email, Model model) {
        model.addAttribute("resetPasswordRequest", new ResetPasswordRequest());
        model.addAttribute("email", email);
        return "reset-password";
    }

    // ✅ Step 6: Save new password
    @PostMapping("/reset-password")
    public String processResetPassword(@Valid @ModelAttribute ResetPasswordRequest resetPasswordRequest,
                                       BindingResult result,
                                       @RequestParam("email") String email,
                                       Model model) {
        if (result.hasErrors()) {
            model.addAttribute("email", email);
            return "reset-password";
        }

        if (!resetPasswordRequest.getPassword().equals(resetPasswordRequest.getConfirmPassword())) {
            model.addAttribute("message", "❌ Passwords do not match");
            model.addAttribute("email", email);
            return "reset-password";
        }

        // update password logic (encode password)
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            model.addAttribute("message", "User not found.");
            return "reset-password";
        }

        user.setPassword(passwordEncoder.encode(resetPasswordRequest.getPassword()));
        userRepository.save(user);

        model.addAttribute("message", "✅ Password updated successfully. You can now login.");
        return "login";
    }




    @GetMapping("/forgot-password-phone")
    public String showPhoneForgotPage(Model model) {
        model.addAttribute("forgotPasswordPhoneRequest", new ForgotPasswordPhoneRequest());
        return "forgot-password-phone";
    }

    @PostMapping("/forgot-password-phone")
    public String processForgotPasswordByPhone(
            @Valid @ModelAttribute("forgotPasswordPhoneRequest") ForgotPasswordPhoneRequest request,
            BindingResult result,
            Model model) {

        if (result.hasErrors()) {
            return "forgot-password-phone";
        }

        String phone = request.getPhoneNumber();

        Optional<User> userOpt = userRepository.findByPhoneNumber(phone);
        if (userOpt.isEmpty()) {
            model.addAttribute("message", "❌ Phone number not found!");
            return "forgot-password-phone";
        }

        String otpCode = otpService.generateOtpForPhone(phone);

        // For now we’ll print the OTP in console (you can integrate SMS later)
        System.out.println("📱 OTP for phone " + phone + " = " + otpCode);

        model.addAttribute("phone", phone);
        return "redirect:/verify-otp-phone?phone=" + phone;
    }




    @GetMapping("/verify-otp-phone")
    public String showVerifyOtpByPhone(@RequestParam("phone") String phone, Model model) {
        model.addAttribute("verifyOtpRequest", new VerifyOtpRequest());
        model.addAttribute("phone", phone);
        return "verify-otp-phone";  // You will create this HTML page
    }

    @PostMapping("/verify-otp-phone")
    public String processVerifyOtpPhone(@Valid @ModelAttribute VerifyOtpRequest verifyOtpRequest,
                                        BindingResult result,
                                        @RequestParam("phone") String phone,
                                        Model model) {
        if (result.hasErrors()) {
            model.addAttribute("phone", phone);
            return "verify-otp-phone";
        }

        boolean isValid = otpService.validateOtpByPhone(phone, verifyOtpRequest.getOtpCode());

        if (!isValid) {
            model.addAttribute("message", "❌ Invalid or expired OTP");
            model.addAttribute("phone", phone);
            return "verify-otp-phone";
        }

        // ✅ Get user email by phone
        Optional<User> userOpt = userRepository.findByPhoneNumber(phone);
        if (userOpt.isPresent()) {
            model.addAttribute("email", userOpt.get().getEmail()); // for reuse
        }

        model.addAttribute("resetPasswordRequest", new ResetPasswordRequest());
        return "reset-password";  // same page reused
    }

}
