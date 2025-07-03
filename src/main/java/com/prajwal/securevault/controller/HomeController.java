package com.prajwal.securevault.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    // ✅ Redirect root URL ("/") to /home
    @GetMapping("/")
    public String redirectToHome() {
        return "redirect:/home";
    }

    // ✅ Display the home page after login with the username
    @GetMapping("/home")
    public String homePage(Authentication authentication, Model model) {
        if (authentication != null && authentication.isAuthenticated()) {
            model.addAttribute("username", authentication.getName());
        } else {
            return "redirect:/login";  // 🛡️ In case user is not authenticated
        }
        return "home";  // Return home.html from templates folder
    }
}
