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

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @GetMapping("/emails/{email}")
    public User getUserByEmail(@PathVariable String email) {
        return userService.getUserByEmail(email);
    }
}
