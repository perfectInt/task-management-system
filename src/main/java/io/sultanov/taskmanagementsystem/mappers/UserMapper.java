package io.sultanov.taskmanagementsystem.mappers;

import io.sultanov.taskmanagementsystem.models.User;
import io.sultanov.taskmanagementsystem.models.dto.RegistrationDto;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {


    public User mapToUser(RegistrationDto registrationDto) {
        User user = new User();
        user.setEmail(registrationDto.getEmail());
        return user;
    }
}
