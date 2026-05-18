package com.rafay.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class SignupDto {
    private String name;
    private String email;
    private String phoneNumber;
    private int age;
    private String photoUrl;
    private String bio;
    private String gender;
    private String location;
    private String password;
}
