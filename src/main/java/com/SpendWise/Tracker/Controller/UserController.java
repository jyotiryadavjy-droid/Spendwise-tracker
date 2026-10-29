
package com.SpendWise.Tracker.Controller;

import com.SpendWise.Tracker.dto.RegisterRequest;
import com.SpendWise.Tracker.service.UserService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Open the registration page
    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "register";
    }

    // Process the registration form
    @PostMapping("/register")
    public String registerUser(
            @ModelAttribute RegisterRequest request,
            Model model) {

        try {
            userService.registerUser(request);

            model.addAttribute(
                    "message",
                    "Registration successful! Please log in."
            );

            model.addAttribute("registerRequest", new RegisterRequest());

            return "register";

        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("registerRequest", request);

            return "register";
        }
    }

    // Open the login page
    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }
}
