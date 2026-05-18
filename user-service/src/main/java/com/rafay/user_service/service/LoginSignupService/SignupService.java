// package com.rafay.user_service.service.LoginSignupService;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.stereotype.Service;

// import com.rafay.user_service.db_entities.AuthCredentials;
// import com.rafay.user_service.db_entities.UserProfileDB;
// import com.rafay.user_service.dto.SignupDto;
// import com.rafay.user_service.repository.AuthCredentialsRepository;
// import com.rafay.user_service.repository.UserProfileDBRepository;

// @Service
// public class SignupService {
//     @Autowired
//     private BCryptPasswordEncoder passwordEncoder;

//     @Autowired
//     private AuthCredentialsRepository authCredentialsRepository;

//     @Autowired
//     private UserProfileDBRepository userProfileDBRepository;

//     public String signup(SignupDto signupDto) {
//         // Check if email already exists
//         if (authCredentialsRepository.findByUserEmail(signupDto.getEmail()).isPresent()) return "Email already registered!";
        

//         // Check if phone number already exists
//         if (userProfileDBRepository.findByPhoneNumber(signupDto.getPhoneNumber()).isPresent())  return "Phone number already registered!";

//         if(userProfileDBRepository.findByName(signupDto.getName()).isPresent()) return "Name already registered!";
        

//     try {
//         AuthCredentials authCredentials = new AuthCredentials();
//         authCredentials.setUserEmail(signupDto.getEmail());
//         authCredentials.setUserPassword(passwordEncoder.encode(signupDto.getPassword()));

        
//         AuthCredentials savedAuthCredentials = authCredentialsRepository.save(authCredentials);
//         UserProfileDB userProfile = new UserProfileDB();
//         userProfile.setAuthCredentials(savedAuthCredentials); // ONLY THIS
//         // userProfile.setUserId(savedAuthCredentials.getUserId());
//         savedAuthCredentials.setUserProfileDB(userProfile);
        
//         userProfile.setName(signupDto.getName());
//         userProfile.setPhoneNumber(signupDto.getPhoneNumber());
//         userProfile.setAge(signupDto.getAge());
//         userProfile.setPhotoUrl(signupDto.getPhotoUrl());
//         userProfile.setBio(signupDto.getBio());
//         userProfile.setGender(signupDto.getGender());
//         userProfile.setLocation(signupDto.getLocation());
        
//         userProfileDBRepository.save(userProfile);

//             return "Signup successful!";
//         } catch (Exception e) {
//             return "Signup failed: " + e.getMessage();
//         }
//     }
// }



package com.rafay.user_service.service.LoginSignupService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rafay.user_service.db_entities.AuthCredentials;
import com.rafay.user_service.db_entities.UserProfileDB;
import com.rafay.user_service.dto.SignupDto;
import com.rafay.user_service.repository.AuthCredentialsRepository;
import com.rafay.user_service.repository.UserProfileDBRepository;

@Service
public class SignupService {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private AuthCredentialsRepository authCredentialsRepository;

    @Autowired
    private UserProfileDBRepository userProfileDBRepository;

    @Transactional
    public String signup(SignupDto signupDto) {

        // Check if email already exists
        if (authCredentialsRepository
                .findByUserEmail(signupDto.getEmail())
                .isPresent()) {
            return "Email already registered!";
        }

        // Check if phone number already exists
        if (userProfileDBRepository
                .findByPhoneNumber(signupDto.getPhoneNumber())
                .isPresent()) {
            return "Phone number already registered!";
        }

        // Check if name already exists
        if (userProfileDBRepository
                .findByName(signupDto.getName())
                .isPresent()) {
            return "Name already registered!";
        }

        try {
            AuthCredentials authCredentials = new AuthCredentials();
            authCredentials.setUserEmail(signupDto.getEmail());
            authCredentials.setUserPassword(
                passwordEncoder.encode(signupDto.getPassword())
            );

            AuthCredentials savedAuth = 
                authCredentialsRepository.save(authCredentials);

            UserProfileDB userProfile = new UserProfileDB();
            userProfile.setAuthCredentials(savedAuth);
            savedAuth.setUserProfileDB(userProfile);
            userProfile.setName(signupDto.getName());
            userProfile.setPhoneNumber(signupDto.getPhoneNumber());
            userProfile.setAge(signupDto.getAge());
            userProfile.setPhotoUrl(signupDto.getPhotoUrl());
            userProfile.setBio(signupDto.getBio());
            userProfile.setGender(signupDto.getGender());
            userProfile.setLocation(signupDto.getLocation());

            userProfileDBRepository.save(userProfile);

            return "Signup successful!";

        } catch (Exception e) {
            return "Signup failed: " + e.getMessage();
        }
    }
}