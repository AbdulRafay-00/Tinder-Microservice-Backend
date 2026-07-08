package com.rafay.user_service.service.notifyservice;

import org.springframework.stereotype.Service;

import com.rafay.user_service.db_entities.AuthCredentials;
import com.rafay.user_service.db_entities.UserProfileDB;
import com.rafay.user_service.dto.NotifyDto;
import com.rafay.user_service.repository.AuthCredentialsRepository;
import com.rafay.user_service.repository.UserProfileDBRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotifyService {

        private final AuthCredentialsRepository authRepository;
        private final UserProfileDBRepository profileRepository;

        public NotifyDto getNotifyData(String userId) {
                if (userId == null || userId.isBlank()) {
                        return new NotifyDto(null, null);
                }

                AuthCredentials auth = authRepository.findById(userId).orElse(null);
                UserProfileDB profile = profileRepository.findById(userId).orElse(null);

                String email = auth == null ? null : auth.getUserEmail();
                String name = profile == null ? null : profile.getName();
                System.out.println("DEBUG email=" + email + " name=" + name);

                return new NotifyDto(email, name);
        }
}
