package com.rafay.Notification_Service.controller;

import com.rafay.Notification_Service.dto.UserDto;
import com.rafay.Notification_Service.dto.UserServiceClientDto;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "user-service")
public interface UserServiceClient {

	@PostMapping("/notify")
	// UserDto getUserById(@RequestBody Object userServiceClientDto);
	UserDto getUserById(@RequestBody UserServiceClientDto userServiceClientDto);

}