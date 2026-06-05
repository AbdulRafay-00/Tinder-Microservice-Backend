package com.rafay.Orchestration_Service.GlobalExeption;

public class NoNearbyUsersException extends RuntimeException {
    public NoNearbyUsersException(String userId) {
        super("No nearby users found for user: " + userId);
    }
}