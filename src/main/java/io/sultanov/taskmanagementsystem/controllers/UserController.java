package io.sultanov.taskmanagementsystem.controllers;

import io.sultanov.taskmanagementsystem.models.User;
import io.sultanov.taskmanagementsystem.models.dto.LoginDto;
import io.sultanov.taskmanagementsystem.models.dto.RegistrationDto;
import io.sultanov.taskmanagementsystem.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/registration")
    public User registerUser(@Valid @RequestBody RegistrationDto registrationDto) {
        return userService.registerUser(registrationDto);
    }

    @PostMapping("/login")
    public String login(@Valid @RequestBody LoginDto loginDto) {
        return userService.login(loginDto);
    }

    @GetMapping("/test")
    public String test() {
        return "secured endpoint";
    }
}
