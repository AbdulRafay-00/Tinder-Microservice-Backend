package com.rafay.user_service.db_entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;



@Data
@NoArgsConstructor
@RequiredArgsConstructor
@Entity
public class UserProfileDB {

    @Id
    private String userId;
    @OneToOne
    @MapsId
    @JoinColumn(name = "userId")
    private AuthCredentials authCredentials;
    @NonNull
    @Column(unique = true, nullable = false)
    private String name;
    @NonNull
    @Column(unique = true, nullable = false)
    private String phoneNumber;
    @NonNull
    @Column( nullable = false)
    private int age;
    @NonNull
    @Column( nullable = false)
    private String photoUrl;
    @NonNull
    @Column( nullable = false)
    private String bio;
    @NonNull
    @Column( nullable = false)
    private String gender;
    @NonNull
    @Column( nullable = false)
    private String location;
}
