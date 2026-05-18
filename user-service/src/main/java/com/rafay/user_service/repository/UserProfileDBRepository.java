package com.rafay.user_service.repository;

import java.util.UUID;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rafay.user_service.db_entities.UserProfileDB;

@Repository
public interface UserProfileDBRepository extends JpaRepository<UserProfileDB, String> {
    Optional<UserProfileDB> findByName(String name);
    Optional<UserProfileDB> findByPhoneNumber(String phoneNumber);
}
