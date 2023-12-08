package io.sultanov.taskmanagementsystem.controller;

import io.sultanov.taskmanagementsystem.exceptions.ObjectNotFoundException;
import io.sultanov.taskmanagementsystem.exceptions.PasswordException;
import io.sultanov.taskmanagementsystem.models.User;
import io.sultanov.taskmanagementsystem.models.dto.LoginDto;
import io.sultanov.taskmanagementsystem.models.dto.RegistrationDto;
import io.sultanov.taskmanagementsystem.services.UserService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Import({UserService.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserControllerTest {

    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    RegistrationDto registrationDto;
    LoginDto loginDto;

    private static final String USERNAME = "test@mail.ru";

    @Autowired
    private UserService userService;

    @Test
    @WithAnonymousUser
    @Order(1)
    @DisplayName("Test for anonymous access")
    public void checkAccessTest() throws Exception {
        RequestBuilder requestBuilderGet = MockMvcRequestBuilders.get("/api/v1/users/12")
                .accept(MediaType.APPLICATION_JSON_VALUE);
        mockMvc.perform(requestBuilderGet).andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    @Order(2)
    @DisplayName("Test for registration with correct data")
    public void registrationTest() throws Exception {
        registrationDto = new RegistrationDto();
        registrationDto.setEmail(USERNAME);
        registrationDto.setPassword("12345678");
        registrationDto.setPasswordConfirmation(registrationDto.getPassword());

        RequestBuilder requestBuilderPost = MockMvcRequestBuilders.post("/api/v1/users/registration")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(registrationDto));

        MvcResult mvcResult = mockMvc.perform(requestBuilderPost)
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        assertNotNull(response);
        User newUser = mapper.readValue(response, User.class);
        assertEquals(registrationDto.getEmail(), newUser.getEmail());
    }

    @Test
    @WithAnonymousUser
    @Order(3)
    @DisplayName("Registration with the same data")
    public void secondRegistrationTest() throws Exception {
        RequestBuilder requestBuilderPost = MockMvcRequestBuilders.post("/api/v1/users/registration")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(registrationDto));

        mockMvc.perform(requestBuilderPost).andExpect(status().isBadRequest());
    }

    @Test
    @Order(4)
    @WithAnonymousUser
    @DisplayName("Test for registration if password and passwordConfirmation are not equal")
    public void passwordRegistrationTest() throws Exception {
        registrationDto.setEmail("anotherEMail@mail.ru");
        registrationDto.setPasswordConfirmation("123456789");
        RequestBuilder requestBuilderPost = MockMvcRequestBuilders.post("/api/v1/users/registration")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(registrationDto));

        MvcResult result = mockMvc.perform(requestBuilderPost).andExpect(status().isBadRequest()).andReturn();
        PasswordException exception = (PasswordException) result.getResolvedException();

        assertNotNull(exception);
    }

    @Test
    @Order(5)
    @WithAnonymousUser
    @DisplayName("Test for registration with incorrect email format")
    public void incorrectEmailFormatRegistrationTest() throws Exception {
        registrationDto.setPasswordConfirmation("12345678");
        registrationDto.setEmail("notemail");

        RequestBuilder requestBuilderPost = MockMvcRequestBuilders.post("/api/v1/users/registration")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(registrationDto));

        mockMvc.perform(requestBuilderPost).andExpect(status().isBadRequest());
    }

    @Test
    @WithAnonymousUser
    @Order(6)
    @DisplayName("Test for login with correct data")
    public void loginTest() throws Exception {
        loginDto = new LoginDto();
        loginDto.setEmail(USERNAME);
        loginDto.setPassword(registrationDto.getPassword());
        RequestBuilder requestBuilderPost = MockMvcRequestBuilders.post("/api/v1/users/login")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(loginDto));

        MvcResult mvcResult = mockMvc.perform(requestBuilderPost)
                .andExpect(status().isOk())
                .andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        assertNotNull(response);
        assertTrue(response.length() > 20);
    }

    @Test
    @Order(7)
    @WithAnonymousUser
    @DisplayName("Test for login throw not existing user")
    public void incorrectEmailLoginTest() throws Exception {
        loginDto.setEmail("l@mail.ru");

        RequestBuilder requestBuilderPost = MockMvcRequestBuilders.post("/api/v1/users/login")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(loginDto));

        mockMvc.perform(requestBuilderPost).andExpect(status().isNotFound());
    }

    @Test
    @Order(7)
    @WithAnonymousUser
    @DisplayName("Test for login with incorrect password")
    public void incorrectPasswordLoginTest() throws Exception {
        loginDto.setEmail(USERNAME);
        loginDto.setPassword("123455678");

        RequestBuilder requestBuilderPost = MockMvcRequestBuilders.post("/api/v1/users/login")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(loginDto));
    }

    @Test
    @Order(8)
    @WithMockUser
    @DisplayName("Test for getting user")
    public void getUserTest() throws Exception {
        RequestBuilder requestBuilderGet = MockMvcRequestBuilders.get("/api/v1/users/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        MvcResult mvcResult = mockMvc.perform(requestBuilderGet).andExpect(status().isOk()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();

        User user = mapper.readValue(response, User.class);
        assertEquals(USERNAME, user.getEmail());
    }

    @Test
    @Order(9)
    @WithMockUser
    @DisplayName("Test for getting non existing user")
    public void getNonExistingUserTest() throws Exception {
        RequestBuilder requestBuilderGet = MockMvcRequestBuilders.get("/api/v1/users/12")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        MvcResult mvcResult = mockMvc.perform(requestBuilderGet).andExpect(status().isNotFound()).andReturn();
        ObjectNotFoundException exception = (ObjectNotFoundException) mvcResult.getResolvedException();

        assertNotNull(exception);
    }

    @Test
    @Order(10)
    @WithMockUser
    @DisplayName("Test to get user by email")
    public void getByEmailTest() throws Exception {
        RequestBuilder requestBuilderGet = MockMvcRequestBuilders.get("/api/v1/users/emails/" + USERNAME)
                .contentType(MediaType.APPLICATION_JSON_VALUE);
        mockMvc.perform(requestBuilderGet).andExpect(status().isOk());

        requestBuilderGet = MockMvcRequestBuilders.get("/api/v1/users/emails/lslle@mail.ru")
                .contentType(MediaType.APPLICATION_JSON_VALUE);
        MvcResult mvcResult = mockMvc.perform(requestBuilderGet).andExpect(status().isNotFound()).andReturn();
        ObjectNotFoundException exception = (ObjectNotFoundException) mvcResult.getResolvedException();

        assertNotNull(exception);
    }
}
