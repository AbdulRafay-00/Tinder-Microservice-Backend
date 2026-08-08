package com.rafay.user_service.controller.LoginSignupController;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import com.rafay.user_service.BaseIntegrationTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class LoginControllerIT extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @Sql(scripts = {"/cleanup.sql", "/sql-scripts/login-scripts/login-test-data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void loginWithValidCredentials_shouldReturnSuccess() throws Exception {
        String requestBody = """
                {
                    "email": "test@example.com",
                    "password": "correctPassword123"
                }
                """;

        mockMvc.perform(post("/login/portal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());
                
    }

    @Test
    @Sql(scripts = {"/cleanup.sql", "/sql-scripts/login-scripts/login-test-data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void loginWithInvalidCredentials_shouldReturnUnauthorized() throws Exception {
        String requestBody = """
                {
                    "email": "test@example.com",
                    "password": "wrongPassword"
                }
                """;

        mockMvc.perform(post("/login/portal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnauthorized());
    }
}