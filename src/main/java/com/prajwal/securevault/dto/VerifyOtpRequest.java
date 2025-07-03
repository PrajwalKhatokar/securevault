package com.prajwal.securevault.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class VerifyOtpRequest {

    @NotBlank(message = "OTP is required")
    @Pattern(regexp = "\\d{6}", message = "OTP must be 6 digits")
    private String otpCode;

    // getter
    public String getOtpCode() {
        return otpCode;
    }

    // setter
    public void setOtpCode(String otpCode) {
        this.otpCode = otpCode;
    }
}
