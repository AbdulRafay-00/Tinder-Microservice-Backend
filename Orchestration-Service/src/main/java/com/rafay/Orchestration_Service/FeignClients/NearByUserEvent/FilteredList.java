package com.rafay.Orchestration_Service.FeignClients.NearByUserEvent;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.rafay.Orchestration_Service.DTO.NearbySearchResultDto;


@FeignClient(name = "match-service")
public interface FilteredList {
    
    @PostMapping("/filter/nearbysearch/list")
    public ResponseEntity<NearbySearchResultDto> acceptNearbyUsers(@RequestBody NearbySearchResultDto request);

//     @PostMapping
// UserDto getUserById(
//         @RequestBody UserServiceClientDto dto
// );
}
