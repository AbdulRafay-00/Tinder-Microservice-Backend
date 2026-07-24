package com.rafay.user_service.service.LoginSignupService;
 
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
 
import java.util.Optional;
 
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
 
import com.rafay.user_service.db_entities.AuthCredentials;
import com.rafay.user_service.db_entities.UserProfileDB;
import com.rafay.user_service.dto.SignupDto;
import com.rafay.user_service.repository.AuthCredentialsRepository;
import com.rafay.user_service.repository.UserProfileDBRepository;
 
@ExtendWith(MockitoExtension.class)
public class SignupServiceTest {
 
    @Mock
    private BCryptPasswordEncoder passwordEncoder;
 
    @Mock
    private AuthCredentialsRepository authCredentialsRepository;
 
    @Mock
    private UserProfileDBRepository userProfileDBRepository;
 
    @InjectMocks
    private SignupService signupService;
 
    private SignupDto signupDto;
 
    @BeforeEach
    void setUp() {
        // Fresh DTO before every test so no test can leak state into another
        signupDto = new SignupDto();
        signupDto.setEmail("rafay@example.com");
        signupDto.setPassword("plainPassword123");
        signupDto.setName("Abdul Rafay");
        signupDto.setPhoneNumber("03001234567");
        signupDto.setAge(22);
        signupDto.setPhotoUrl("http://example.com/photo.jpg");
        signupDto.setBio("Backend dev in progress");
        signupDto.setGender("Male");
        signupDto.setLocation("Karachi");
    }
 
    // ---------- HAPPY PATH ----------
 
    @Test
    void signup_shouldSucceed_whenEmailPhoneAndNameAreUnique() {
        // Arrange: nothing exists yet, so all uniqueness checks pass
        when(authCredentialsRepository.findByUserEmail(signupDto.getEmail()))
                .thenReturn(Optional.empty());
        when(userProfileDBRepository.findByPhoneNumber(signupDto.getPhoneNumber()))
                .thenReturn(Optional.empty());
        when(userProfileDBRepository.findByName(signupDto.getName()))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode(signupDto.getPassword()))
                .thenReturn("hashedPassword");
 
        AuthCredentials savedAuth = new AuthCredentials();
        savedAuth.setUserEmail(signupDto.getEmail());
        savedAuth.setUserPassword("hashedPassword");
        when(authCredentialsRepository.save(any(AuthCredentials.class)))
                .thenReturn(savedAuth);
 
        // Act
        String result = signupService.signup(signupDto);
 
        // Assert
        assertEquals("Signup successful!", result);
        verify(passwordEncoder).encode("plainPassword123");
        verify(authCredentialsRepository).save(any(AuthCredentials.class));
        verify(userProfileDBRepository).save(any(UserProfileDB.class));
    }
 
    @Test
    void signup_shouldMapAllDtoFieldsCorrectly_ontoUserProfile() {
        // This test exists to catch silent field-mapping bugs (like the
        // DTO/entity name mismatch you hit in the notification service)
        when(authCredentialsRepository.findByUserEmail(anyString()))
                .thenReturn(Optional.empty());
        when(userProfileDBRepository.findByPhoneNumber(anyString()))
                .thenReturn(Optional.empty());
        when(userProfileDBRepository.findByName(anyString()))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(authCredentialsRepository.save(any(AuthCredentials.class)))
                .thenReturn(new AuthCredentials());
 
        signupService.signup(signupDto);
 
        ArgumentCaptor<UserProfileDB> captor = ArgumentCaptor.forClass(UserProfileDB.class);
        verify(userProfileDBRepository).save(captor.capture());
 
        UserProfileDB savedProfile = captor.getValue();
        assertEquals(signupDto.getName(), savedProfile.getName());
        assertEquals(signupDto.getPhoneNumber(), savedProfile.getPhoneNumber());
        assertEquals(signupDto.getAge(), savedProfile.getAge());
        assertEquals(signupDto.getPhotoUrl(), savedProfile.getPhotoUrl());
        assertEquals(signupDto.getBio(), savedProfile.getBio());
        assertEquals(signupDto.getGender(), savedProfile.getGender());
        assertEquals(signupDto.getLocation(), savedProfile.getLocation());
    }
 
    @Test
    void signup_shouldLinkAuthCredentialsAndUserProfile_bidirectionally() {
        // Verifies the two-way relationship is actually set both ways,
        // not just one side (easy to get subtly wrong with JPA entities)
        when(authCredentialsRepository.findByUserEmail(anyString()))
                .thenReturn(Optional.empty());
        when(userProfileDBRepository.findByPhoneNumber(anyString()))
                .thenReturn(Optional.empty());
        when(userProfileDBRepository.findByName(anyString()))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
 
        AuthCredentials savedAuth = new AuthCredentials();
        when(authCredentialsRepository.save(any(AuthCredentials.class)))
                .thenReturn(savedAuth);
 
        signupService.signup(signupDto);
 
        ArgumentCaptor<UserProfileDB> captor = ArgumentCaptor.forClass(UserProfileDB.class);
        verify(userProfileDBRepository).save(captor.capture());
 
        UserProfileDB savedProfile = captor.getValue();
        assertSame(savedAuth, savedProfile.getAuthCredentials());
        assertSame(savedProfile, savedAuth.getUserProfileDB());
    }
 
    // ---------- DUPLICATE CHECKS (each should short-circuit before any save) ----------
 
    @Test
    void signup_shouldFail_whenEmailAlreadyExists() {
        when(authCredentialsRepository.findByUserEmail(signupDto.getEmail()))
                .thenReturn(Optional.of(new AuthCredentials()));
 
        String result = signupService.signup(signupDto);
 
        assertEquals("Email already registered!", result);
        // Nothing past the email check should ever run
        verify(userProfileDBRepository, never()).findByPhoneNumber(anyString());
        verify(userProfileDBRepository, never()).findByName(anyString());
        verify(authCredentialsRepository, never()).save(any());
        verify(userProfileDBRepository, never()).save(any());
    }
 
    @Test
    void signup_shouldFail_whenPhoneNumberAlreadyExists() {
        when(authCredentialsRepository.findByUserEmail(signupDto.getEmail()))
                .thenReturn(Optional.empty());
        when(userProfileDBRepository.findByPhoneNumber(signupDto.getPhoneNumber()))
                .thenReturn(Optional.of(new UserProfileDB()));
 
        String result = signupService.signup(signupDto);
 
        assertEquals("Phone number already registered!", result);
        verify(userProfileDBRepository, never()).findByName(anyString());
        verify(authCredentialsRepository, never()).save(any());
        verify(userProfileDBRepository, never()).save(any());
    }
 
    @Test
    void signup_shouldFail_whenNameAlreadyExists() {
        when(authCredentialsRepository.findByUserEmail(signupDto.getEmail()))
                .thenReturn(Optional.empty());
        when(userProfileDBRepository.findByPhoneNumber(signupDto.getPhoneNumber()))
                .thenReturn(Optional.empty());
        when(userProfileDBRepository.findByName(signupDto.getName()))
                .thenReturn(Optional.of(new UserProfileDB()));
 
        String result = signupService.signup(signupDto);
 
        assertEquals("Name already registered!", result);
        verify(authCredentialsRepository, never()).save(any());
        verify(userProfileDBRepository, never()).save(any());
    }
 
    @Test
    void signup_shouldCheckUniquenessInOrder_emailThenPhoneThenName() {
        // Locks in the exact order of checks so a future refactor
        // doesn't silently swap the order and change which error a user sees
        when(authCredentialsRepository.findByUserEmail(signupDto.getEmail()))
                .thenReturn(Optional.of(new AuthCredentials()));
 
        String result = signupService.signup(signupDto);
 
        assertEquals("Email already registered!", result);
        verify(userProfileDBRepository, never()).findByPhoneNumber(anyString());
    }
 
    // ---------- EXCEPTION HANDLING ----------
 
    @Test
    void signup_shouldReturnFailureMessage_whenAuthCredentialsSaveThrows() {
        when(authCredentialsRepository.findByUserEmail(anyString()))
                .thenReturn(Optional.empty());
        when(userProfileDBRepository.findByPhoneNumber(anyString()))
                .thenReturn(Optional.empty());
        when(userProfileDBRepository.findByName(anyString()))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(authCredentialsRepository.save(any(AuthCredentials.class)))
                .thenThrow(new RuntimeException("DB connection lost"));
 
        String result = signupService.signup(signupDto);
 
        assertTrue(result.startsWith("Signup failed: "));
        assertTrue(result.contains("DB connection lost"));
        verify(userProfileDBRepository, never()).save(any());
    }
 
    @Test
    void signup_shouldReturnFailureMessage_whenUserProfileSaveThrows() {
        when(authCredentialsRepository.findByUserEmail(anyString()))
                .thenReturn(Optional.empty());
        when(userProfileDBRepository.findByPhoneNumber(anyString()))
                .thenReturn(Optional.empty());
        when(userProfileDBRepository.findByName(anyString()))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(authCredentialsRepository.save(any(AuthCredentials.class)))
                .thenReturn(new AuthCredentials());
        when(userProfileDBRepository.save(any(UserProfileDB.class)))
                .thenThrow(new RuntimeException("Duplicate entry for phone_number"));
 
        String result = signupService.signup(signupDto);
 
        assertTrue(result.startsWith("Signup failed: "));
        assertTrue(result.contains("Duplicate entry for phone_number"));
    }
}
 