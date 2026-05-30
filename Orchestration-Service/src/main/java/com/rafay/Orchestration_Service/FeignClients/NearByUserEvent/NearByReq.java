package com.rafay.Orchestration_Service.FeignClients.NearByUserEvent;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.rafay.Orchestration_Service.DTO.NearLocationRequestDTO;
import com.rafay.Orchestration_Service.DTO.NearbySearchResultDto;

@FeignClient(name = "locationService")
public interface NearByReq {

    @PostMapping("/location/nearby-search/sync")
    ResponseEntity<NearbySearchResultDto> getNearbySearchSync(@RequestBody NearLocationRequestDTO request);
}
