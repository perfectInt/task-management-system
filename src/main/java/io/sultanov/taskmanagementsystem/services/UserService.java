package io.sultanov.taskmanagementsystem.services;

import io.sultanov.taskmanagementsystem.exceptions.ObjectAlreadyExistsException;
import io.sultanov.taskmanagementsystem.exceptions.ObjectNotFoundException;
import io.sultanov.taskmanagementsystem.exceptions.PasswordException;
import io.sultanov.taskmanagementsystem.mappers.UserMapper;
import io.sultanov.taskmanagementsystem.models.User;
import io.sultanov.taskmanagementsystem.models.dto.LoginDto;
import io.sultanov.taskmanagementsystem.models.dto.RegistrationDto;
import io.sultanov.taskmanagementsystem.repositories.UserRepository;
import io.sultanov.taskmanagementsystem.security.config.JwtService;
import io.sultanov.taskmanagementsystem.security.utils.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserMapper mapper;

    @Transactional
    public User registerUser(RegistrationDto registrationDto) {
        if (userRepository.findByEmail(registrationDto.getEmail()).isPresent())
            throw new ObjectAlreadyExistsException("User with " + registrationDto.getEmail() + " email already exists!");
        if (!Objects.equals(registrationDto.getPassword(), registrationDto.getPasswordConfirmation()))
            throw new PasswordException("Passwords are not matching!");
        User user = mapper.mapToUser(registrationDto);
        log.debug("{}", user);
        user.setPassword(encoder.encode(registrationDto.getPassword()));
        user.setRole(Role.USER);
        return userRepository.save(user);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() ->
                new ObjectNotFoundException("User with this id does not exist"));
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() ->
                new ObjectNotFoundException("User with this email does not exist"));
    }

    public List<User> getExecutors(List<String> emails) {
        return userRepository.findAllByEmailInArgs(emails);
    }

    public String login(LoginDto loginDto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getEmail(),
                        loginDto.getPassword()
                )
        );
        var user = getUserByEmail(loginDto.getEmail());
        return jwtService.generateToken(user);
    }
}
