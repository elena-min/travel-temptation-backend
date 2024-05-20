package org.individualproject.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.individualproject.TravelApplication;
import org.individualproject.business.UserService;
import org.individualproject.domain.CreateUserRequest;
import org.individualproject.domain.UpdateUserRequest;
import org.individualproject.domain.User;
import org.individualproject.domain.enums.Gender;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@SpringBootTest(classes = TravelApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserControllerIntegrationTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createUser_shouldCreateAndReturn201_WhenRequestValid() throws Exception {
        CreateUserRequest request = new CreateUserRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setBirthDate(LocalDate.of(1990, 1, 1));
        request.setEmail("john.doe@example.com");
        request.setPassword("password");
        request.setGender(Gender.MALE);

        User user = new User(1L, "John", "Doe", LocalDate.of(1990, 1, 1), "john.doe@example.com", "hashedPassword", Gender.MALE);
        when(userService.createUser(any(CreateUserRequest.class))).thenReturn(user);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }

    @Test
    void getUsers_shouldReturn200WithUsersList() throws Exception {
        LocalDate date = LocalDate.of(2014, 9, 16);

        List<User> expected =  Arrays.asList(
                new User(1L, "John", "Doe", date, "j.doe@example.com", "hashedPassword1", Gender.MALE ),
                new User(2L, "Eve", "McDonalds", date, "e.mcdonalds@example.com", "hashedPassword2", Gender.FEMALE),
                new User(3L, "Donald", "Duck", date, "d.duck@example.com", "hashedPassword3",  Gender.MALE)
        );
        when(userService.getUsers()).thenReturn(expected);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(3))) // Asserting that there are 3 users in the response
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[0].lastName").value("Doe"))
                .andExpect(jsonPath("$[0].email").value("j.doe@example.com"))
                .andExpect(jsonPath("$[1].firstName").value("Eve"))
                .andExpect(jsonPath("$[1].lastName").value("McDonalds"))
                .andExpect(jsonPath("$[1].email").value("e.mcdonalds@example.com"))
                .andExpect(jsonPath("$[2].firstName").value("Donald"))
                .andExpect(jsonPath("$[2].lastName").value("Duck"))
                .andExpect(jsonPath("$[2].email").value("d.duck@example.com"));

        verify(userService).getUsers();
    }

    @Test
    void getUser_shouldReturn200WithUser_whenUserFound() throws Exception {
        User user = new User(1L, "John", "Doe", LocalDate.of(1990, 1, 1), "john.doe@example.com", "hashedPassword", Gender.MALE);

        when(userService.getUser(1L)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/users/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));

        verify(userService).getUser(1L);
    }

    @Test
    @WithMockUser(username = "admin@fontys.nl", roles = {"ADMIN"})
    void deleteUser_shouldReturnOk_whenUserExists() throws Exception {
        mockMvc.perform(delete("/users/100"))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(userService).deleteUser(100L);
    }

    @Test
    @WithMockUser(username = "admin@fontys.nl", roles = {"ADMIN"})
    void deleteUser_shouldReturnNotFound_whenUserDoesNotExist() throws Exception {
        long userId = 1L;

        when(userService.deleteUser(userId)).thenReturn(false);

        mockMvc.perform(delete("/users/{id}", userId))
                .andExpect(status().isNotFound());


        verify(userService).deleteUser(userId);
    }

    @Test
    @WithMockUser(username = "admin@fontys.nl", roles = {"ADMIN"})
    void updateUser_shouldReturn204() throws Exception {
        long userId = 1L;
        UpdateUserRequest request = new UpdateUserRequest(userId, "John", "Doe", LocalDate.of(1990, 1, 1), Gender.MALE);

        // Performing PUT request
        mockMvc.perform(put("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        verify(userService).updateUser(request);
    }

    @Test
    @WithMockUser(username = "admin@fontys.nl", roles = {"ADMIN"})
    void updateUser_shouldReturnBadRequest_whenRequestIsInvalid() throws Exception {
        long userId = 1L;
        UpdateUserRequest request = new UpdateUserRequest(userId, "", "Doe", LocalDate.of(1990, 1, 1), Gender.MALE);

        // Performing PUT request with invalid request
        mockMvc.perform(put("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        // Verifying that the userService.updateUser method is not called with invalid request
        verify(userService, never()).updateUser(request);
    }

}
