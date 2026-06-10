package com.rafay.user_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.rafay.user_service.db_entities.AuthCredentials;

@Repository
public interface AuthCredentialsRepository extends JpaRepository<AuthCredentials, String> {
    Optional<AuthCredentials> findByUserEmail(String userEmail);

    @Query("SELECT u.userEmail FROM AuthCredentials u WHERE u.userId = :id")
    String findUserEmailById(@Param("id") String id);
}
