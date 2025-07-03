package com.prajwal.securevault.controller;

import com.prajwal.securevault.dto.ForgotPasswordPhoneRequest;
import com.prajwal.securevault.dto.VerifyPhoneOtpRequest;
import com.prajwal.securevault.dto.ResetPasswordByPhoneRequest;
import com.prajwal.securevault.service.OtpService;
import com.prajwal.securevault.service.PhoneResetService;
import com.prajwal.securevault.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

//THIS IS FOR PHONE RESET
@Controller
public class PhoneResetController {

    @Autowired
    private OtpService otpService;

    @Autowired
    private PhoneResetService phoneResetService;

    @Autowired
    private EmailService emailService;

    @GetMapping("/forgot-by-phone")
    public String showPhoneForm(Model model) {
        model.addAttribute("forgotByPhoneRequest", new ForgotPasswordPhoneRequest());
        return "forgot-by-phone";
    }

    @PostMapping("/forgot-by-phone")
    public String sendOtp(@Valid @ModelAttribute ForgotPasswordPhoneRequest request,
                          BindingResult result,
                          Model model) {

        if (result.hasErrors()) return "forgot-by-phone";

        return phoneResetService.getUserByPhone(request.getPhoneNumber()).map(user -> {
            String otp = otpService.generateOtpForPhone(request.getPhoneNumber());
            // 🔔 Instead of SMS API, just simulate by printing/logging or sending to your email
            emailService.sendOtpEmail(user.getEmail(), otp);
            model.addAttribute("verifyPhoneOtpRequest", new VerifyPhoneOtpRequest());
            return "verify-phone-otp";
        }).orElseGet(() -> {
            model.addAttribute("message", "Phone number not found!");
            return "forgot-by-phone";
        });
    }

    @PostMapping("/verify-phone-otp")
    public String verifyOtp(@ModelAttribute VerifyPhoneOtpRequest request,
                            Model model) {
        boolean valid = otpService.validateOtpByPhone(request.getPhoneNumber(), request.getOtpCode());

        if (valid) {
            ResetPasswordByPhoneRequest reset = new ResetPasswordByPhoneRequest();
            reset.setPhoneNumber(request.getPhoneNumber());
            model.addAttribute("resetPasswordByPhoneRequest", reset);
            return "reset-password-by-phone";
        }

        model.addAttribute("message", "Invalid or expired OTP.");
        model.addAttribute("verifyPhoneOtpRequest", request);
        return "verify-phone-otp";
    }

    @PostMapping("/reset-password-by-phone")
    public String resetPassword(@Valid @ModelAttribute ResetPasswordByPhoneRequest request,
                                BindingResult result,
                                Model model) {

        if (result.hasErrors()) return "reset-password-by-phone";

        phoneResetService.resetPassword(request.getPhoneNumber(), request.getNewPassword());
        model.addAttribute("message", "✅ Password updated! Please login.");
        return "login";
    }
}
