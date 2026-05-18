package com.rafay.user_service.db_entities;

import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
@Entity
public class AuthCredentials {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Setter(AccessLevel.NONE)
    private String userId;
    @NonNull
    @Column(unique = true, nullable = false)
    private String userEmail;
    @NonNull
    @Column(nullable = false)
    private String userPassword;
    @OneToOne(mappedBy = "authCredentials", cascade = CascadeType.ALL, orphanRemoval = true)
    private UserProfileDB userProfileDB;

    @OneToOne(mappedBy = "authCredentials", cascade = CascadeType.ALL, orphanRemoval = true)
    private LiveLocationDB liveLocationDB;
}
