package io.sultanov.taskmanagementsystem.services;

import io.sultanov.taskmanagementsystem.exceptions.ObjectAlreadyExistsException;
import io.sultanov.taskmanagementsystem.exceptions.ObjectNotFoundException;
import io.sultanov.taskmanagementsystem.exceptions.PasswordException;
import io.sultanov.taskmanagementsystem.models.User;
import io.sultanov.taskmanagementsystem.models.dto.LoginDto;
import io.sultanov.taskmanagementsystem.models.dto.RegistrationDto;
import io.sultanov.taskmanagementsystem.repositories.UserRepository;
import io.sultanov.taskmanagementsystem.security.config.JwtService;
import io.sultanov.taskmanagementsystem.security.utils.Role;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserMapper mapper = Mappers.getMapper(UserMapper.class);

    @Transactional
    public User registerUser(RegistrationDto registrationDto) {
        if (userRepository.findByEmail(registrationDto.getEmail()).isPresent())
            throw new ObjectAlreadyExistsException("User with " + registrationDto.getEmail() + " email already exists!");
        if (!Objects.equals(registrationDto.getPassword(), registrationDto.getPasswordConfirmation()))
            throw new PasswordException("Passwords are not matching!");
        User user = mapper.mapToUser(registrationDto);
        user.setPassword(encoder.encode(registrationDto.getPassword()));
        user.setRole(Role.USER);
        return userRepository.save(user);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(ObjectNotFoundException::new);
    }

    public String login(LoginDto loginDto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getEmail(),
                        loginDto.getPassword()
                )
        );
        var user = userRepository.findByEmail(loginDto.getEmail()).orElseThrow(ObjectNotFoundException::new);
        return jwtService.generateToken(user);
    }

    @Mapper(componentModel = "spring")
    public interface UserMapper {
        User mapToUser(RegistrationDto registrationDto);
    }
}
