package io.sultanov.taskmanagementsystem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.sultanov.taskmanagementsystem.mappers.TaskMapper;
import io.sultanov.taskmanagementsystem.models.Task;
import io.sultanov.taskmanagementsystem.models.dto.ChangeStatusDto;
import io.sultanov.taskmanagementsystem.models.dto.CommentDto;
import io.sultanov.taskmanagementsystem.models.dto.RegistrationDto;
import io.sultanov.taskmanagementsystem.models.dto.TaskDto;
import io.sultanov.taskmanagementsystem.repositories.TaskRepository;
import io.sultanov.taskmanagementsystem.services.TaskService;
import io.sultanov.taskmanagementsystem.services.UserService;
import io.sultanov.taskmanagementsystem.utils.Priority;
import io.sultanov.taskmanagementsystem.utils.Status;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Import({UserService.class, TaskService.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private TaskMapper taskMapper;

    ObjectMapper mapper = new ObjectMapper();

    TaskDto taskDto;

    private static final String USERNAME = "test@mail.ru";

    @BeforeAll
    public void init() {
        // first user
        RegistrationDto registrationDto = new RegistrationDto();
        registrationDto.setEmail(USERNAME);
        registrationDto.setPassword("12345678");
        registrationDto.setPasswordConfirmation("12345678");
        userService.registerUser(registrationDto);

        // second user
        registrationDto.setEmail("test2@mail.ru");
        registrationDto.setPassword("12345678");
        registrationDto.setPasswordConfirmation("12345678");
        userService.registerUser(registrationDto);
    }

    @Test
    @WithMockUser(username = USERNAME)
    @Order(1)
    @DisplayName("Test for creating a task")
    public void createTaskTest() throws Exception {
        taskDto = new TaskDto();
        taskDto.setTitle("Test Task");
        taskDto.setDescription("Test description");
        taskDto.setExecutors(List.of("test2@mail.ru"));
        taskDto.setStatus(Status.PENDING);
        taskDto.setPriority(Priority.MEDIUM);

        RequestBuilder requestBuilderPost = MockMvcRequestBuilders.post("/api/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(taskDto));

        MvcResult mvcResult = mockMvc.perform(requestBuilderPost)
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        assertNotNull(response);
        Task createdTask = mapper.readValue(response, Task.class);
        assertNotNull(createdTask.getId());
        assertEquals(taskDto.getTitle(), createdTask.getTitle());
        assertEquals(USERNAME, createdTask.getAuthor());
        assertEquals(1, createdTask.getExecutors().size());
        assertEquals(0, createdTask.getComments().size());
    }

    @Test
    @WithMockUser(username = USERNAME)
    @Order(2)
    @DisplayName("Test for editing a task by user")
    public void editTaskTest() throws Exception {
        taskDto.setId(1L);
        taskDto.setTitle("New title");

        RequestBuilder requestBuilderPut = MockMvcRequestBuilders.put("/api/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(taskDto));

        MvcResult mvcResult = mockMvc.perform(requestBuilderPut).andExpect(status().isOk()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();

        Task task = mapper.readValue(response, Task.class);
        assertEquals(USERNAME, task.getAuthor());
        assertEquals("New title", task.getTitle());
        assertEquals(1, task.getId());
    }

    @Test
    @WithMockUser(username = USERNAME)
    @Order(3)
    @DisplayName("Test for comments")
    public void commentTest() throws Exception {
        CommentDto commentDto = new CommentDto();
        commentDto.setComment("New comment");
        RequestBuilder requestBuilderPut = MockMvcRequestBuilders.put("/api/v1/tasks/comments?task_id=" + taskDto.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(commentDto));

        MvcResult result = mockMvc.perform(requestBuilderPut).andExpect(status().isOk()).andReturn();
        String response = result.getResponse().getContentAsString();

        Task task = mapper.readValue(response, Task.class);
        assertEquals(1, task.getComments().size());
    }

    @Test
    @WithMockUser(username = USERNAME)
    @Order(4)
    @DisplayName("Test with user's task (with pagination)")
    public void getUsersTaskWithPaginationTest() throws Exception {
        RequestBuilder requestBuilderGet = MockMvcRequestBuilders.get("/api/v1/tasks?page=0")
                .accept(MediaType.APPLICATION_JSON_VALUE);
        mockMvc.perform(requestBuilderGet).andExpect(status().isOk())
                .andExpect(jsonPath("length()").value(1));

        requestBuilderGet = MockMvcRequestBuilders.get("/api/v1/tasks?page=1")
                .accept(MediaType.APPLICATION_JSON_VALUE);
        mockMvc.perform(requestBuilderGet).andExpect(status().isOk())
                .andExpect(jsonPath("length()").value(0));
    }

    @Test
    @WithMockUser(username = "anotherUser@mail.ru")
    @Order(5)
    @DisplayName("Test editing other user's tasks")
    public void editOtherUserTasksTest() throws Exception {
        RequestBuilder requestBuilderPut = MockMvcRequestBuilders.put("/api/v1/tasks")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(taskDto));

        mockMvc.perform(requestBuilderPut).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    @Order(6)
    @DisplayName("Test for getting non existing tasks")
    public void getNonExistingTasks() throws Exception {
        RequestBuilder requestBuilderGet = MockMvcRequestBuilders.get("/api/v1/tasks/21312")
                .accept(MediaType.APPLICATION_JSON_VALUE);

        mockMvc.perform(requestBuilderGet).andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = USERNAME)
    @Order(7)
    @DisplayName("Test with user's task by author (with pagination)")
    public void getUsersTaskByAuthorWithPaginationTest() throws Exception {
        RequestBuilder requestBuilderGet = MockMvcRequestBuilders.get("/api/v1/tasks/authors?author=" + USERNAME)
                .accept(MediaType.APPLICATION_JSON_VALUE);
        mockMvc.perform(requestBuilderGet).andExpect(status().isOk())
                .andExpect(jsonPath("length()").value(1));

        requestBuilderGet = MockMvcRequestBuilders.get("/api/v1/tasks/authors?author=" + USERNAME + "?page=1")
                .accept(MediaType.APPLICATION_JSON_VALUE);
        mockMvc.perform(requestBuilderGet).andExpect(status().isOk())
                .andExpect(jsonPath("length()").value(0));
    }

    @Test
    @WithMockUser(username = USERNAME)
    @Order(7)
    @DisplayName("Test with user's task by author (with pagination)")
    public void getUsersTaskByExecutorWithPaginationTest() throws Exception {
        RequestBuilder requestBuilderGet = MockMvcRequestBuilders.get("/api/v1/tasks/executors?executor=test2@mail.ru")
                .accept(MediaType.APPLICATION_JSON_VALUE);
        mockMvc.perform(requestBuilderGet).andExpect(status().isOk())
                .andExpect(jsonPath("length()").value(1));

        requestBuilderGet = MockMvcRequestBuilders.get("/api/v1/tasks/executors?executor=test2@mail.ru?page=1")
                .accept(MediaType.APPLICATION_JSON_VALUE);
        mockMvc.perform(requestBuilderGet).andExpect(status().isOk())
                .andExpect(jsonPath("length()").value(0));
    }

    @Test
    @WithMockUser(username = "test2@mail.ru")
    @Order(8)
    @DisplayName("Test to change task status with other user")
    public void changeTaskStatusByExecutorTest() throws Exception {
        ChangeStatusDto changeStatusDto = new ChangeStatusDto();
        changeStatusDto.setId(1L);
        changeStatusDto.setStatus(Status.COMPLETED);
        RequestBuilder requestBuilderPut = MockMvcRequestBuilders.put("/api/v1/tasks/status")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(changeStatusDto));

        MvcResult mvcResult = mockMvc.perform(requestBuilderPut).andExpect(status().isOk()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        assertNotNull(response);

        Task task = mapper.readValue(response, Task.class);
        assertEquals(changeStatusDto.getId(), task.getId());
        assertEquals(changeStatusDto.getStatus(), task.getStatus());
    }

    @Test
    @WithMockUser(username = "test3@mail.ru")
    @Order(9)
    @DisplayName("Test to change task status with other user")
    public void changeTaskStatusByOtherUserTest() throws Exception {
        ChangeStatusDto changeStatusDto = new ChangeStatusDto();
        changeStatusDto.setId(1L);
        changeStatusDto.setStatus(Status.COMPLETED);
        RequestBuilder requestBuilderPut = MockMvcRequestBuilders.put("/api/v1/tasks/status")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(changeStatusDto));

        mockMvc.perform(requestBuilderPut).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "test3@mail.ru")
    @Order(9)
    @DisplayName("Test to delete task with other user")
    public void deleteTaskStatusByOtherUserTest() throws Exception {
        RequestBuilder requestBuilderDelete = MockMvcRequestBuilders.delete("/api/v1/tasks/1");
        mockMvc.perform(requestBuilderDelete).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = USERNAME)
    @Order(10)
    @DisplayName("Test to delete task with user")
    public void deleteTaskStatusByOwnerTest() throws Exception {
        RequestBuilder requestBuilderDelete = MockMvcRequestBuilders.delete("/api/v1/tasks/1");
        mockMvc.perform(requestBuilderDelete).andExpect(status().isOk());
    }
}
