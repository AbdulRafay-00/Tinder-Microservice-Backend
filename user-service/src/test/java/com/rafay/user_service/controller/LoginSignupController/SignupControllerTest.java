package com.rafay.user_service.controller.LoginSignupController;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.rafay.user_service.dto.SignupDto;
import com.rafay.user_service.service.LoginSignupService.SignupService;
@ExtendWith(MockitoExtension.class)
public class SignupControllerTest {

    @Mock
    private SignupService signupService;

    @InjectMocks
    private SignupController signupController;

    @Test
    @DisplayName("Should return success message when signup data is valid")
    void signup_validDetails_returnsSuccessMessage() {
        // Arrange
        SignupDto signupDto = new SignupDto();
        signupDto.setEmail("newuser@example.com");
        signupDto.setPassword("password");
        signupDto.setName("newuser");
        signupDto.setPhoneNumber("1234567890");
        signupDto.setAge(25);
        signupDto.setGender("Male");
        // signupDto.setCity("Karachi");
        // signupDto.setCountry("Pakistan");
        // signupDto.setProfilePicture("profile.jpg");
        signupDto.setBio("This is a bio");
        signupDto.setLocation("Karachi, Pakistan");
        signupDto.setPhotoUrl("photo.jpg");

        when(signupService.signup(signupDto)).thenReturn("Signup successful");

        // Act
        String result = signupController.signup(signupDto);

        // Assert
        assertEquals("Signup successful", result);
        verify(signupService).signup(signupDto);
    }

    @Test
    @DisplayName("Should return failure message when email already exists")
    void signup_duplicateEmail_returnsFailureMessage() {
        SignupDto signupDto = new SignupDto();
        signupDto.setEmail("existing@example.com");
        signupDto.setPassword("password");

        when(signupService.signup(signupDto)).thenReturn("Email already registered");

        String result = signupController.signup(signupDto);

        assertEquals("Email already registered", result);
    }
}
