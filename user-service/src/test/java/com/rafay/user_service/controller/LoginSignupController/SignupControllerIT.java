package com.rafay.user_service.controller.LoginSignupController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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

    

}
