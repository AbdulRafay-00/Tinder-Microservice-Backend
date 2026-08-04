package com.rafay.user_service.controller.LoginSignupController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import com.rafay.user_service.BaseIntegrationTest;

public class SignupControllerIT extends BaseIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void signupWithValidValues_shouldReturnSuccess() throws Exception {
        String requestBody = """
                                {
                "name": "Abdul Rafay",
                "email": "abdul.rafay@example.com",
                "phoneNumber": "03001234567",
                "age": 22,
                "photoUrl": "https://example.com/images/profile.jpg",
                "bio": "Software Engineering student and Java developer.",
                "gender": "Male",
                "location": "Karachi",
                "password": "Password@123"
                }
                    """;

        mockMvc.perform(post("/signup/service")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk());

    }

    @Test
   @Sql(
    scripts = {
        "/cleanup.sql",
        "/sql-scripts/signup-scripts/duplicate-email.sql"
    },
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
    void signupWithDuplicateEmail_shouldReturnConflict() throws Exception {

        String requestBody = """
                {
                    "name": "Another User",
                    "email": "test@example.com",
                    "phoneNumber": "03111222333",
                    "age": 22,
                    "photoUrl": "https://example.com/profile.jpg",
                    "bio": "Test Bio",
                    "gender": "Male",
                    "location": "Karachi",
                    "password": "Password@123"
                }
                """;

        mockMvc.perform(post("/signup/service")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isConflict())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Email already registered!")));
    }

    @Test
    @Sql(
    scripts = {
        "/cleanup.sql",
        "/sql-scripts/signup-scripts/duplicate-phone.sql"
    },
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
    void signupWithDuplicatePhoneNumber_shouldReturnConflict() throws Exception {

        String requestBody = """
                {
                    "name": "Another User",
                    "email": "another@example.com",
                    "phoneNumber": "03001234567",
                    "age": 22,
                    "photoUrl": "https://example.com/profile.jpg",
                    "bio": "Test Bio",
                    "gender": "Male",
                    "location": "Karachi",
                    "password": "Password@123"
                }
                """;

        mockMvc.perform(post("/signup/service")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isConflict())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Phone number already registered!")));
    }

    @Test
   @Sql(
    scripts = {
        "/cleanup.sql",
        "/sql-scripts/signup-scripts/duplicate-name.sql"
    },
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
    void signupWithDuplicateName_shouldReturnConflict() throws Exception {

        String requestBody = """
                {
                    "name": "Test User",
                    "email": "another@example.com",
                    "phoneNumber": "03111222333",
                    "age": 22,
                    "photoUrl": "https://example.com/profile.jpg",
                    "bio": "Test Bio",
                    "gender": "Male",
                    "location": "Karachi",
                    "password": "Password@123"
                }
                """;

        mockMvc.perform(post("/signup/service")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isConflict())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Name already registered!")));
    }

}
