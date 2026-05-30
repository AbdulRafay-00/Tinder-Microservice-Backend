package com.rafay.Orchestration_Service.service;

import org.springframework.stereotype.Service;

import com.rafay.Orchestration_Service.DTO.LocationRequset;
import com.rafay.Orchestration_Service.DTO.NearLocationRequestDTO;
import com.rafay.Orchestration_Service.DTO.NearbySearchResultDto;
import com.rafay.Orchestration_Service.FeignClients.NearByUserEvent.NearByReq;
import com.rafay.Orchestration_Service.GlobalExeption.NoNearbyUsersException;
@Service
public class NearUserList {

    private final NearByReq nearByReq;

    // ✅ Only inject Feign Client, not DTO
    public NearUserList(NearByReq nearByReq) {
        this.nearByReq = nearByReq;
    }

    public NearbySearchResultDto getNearUserList(LocationRequset locationRequest, String userId) {
        
        // ✅ Create DTO inside method, not injected
        NearLocationRequestDTO nearLocationRequestDTO = new NearLocationRequestDTO();
        nearLocationRequestDTO.setUserId(userId);
        nearLocationRequestDTO.setLatitude(locationRequest.getLatitude());
        nearLocationRequestDTO.setLongitude(locationRequest.getLongitude());

        // call Location Service
        NearbySearchResultDto nearbyResult = nearByReq.getNearbySearchSync(nearLocationRequestDTO).getBody();

        // check if empty
        if (nearbyResult == null || nearbyResult.getNearbyUserIds().isEmpty()) {
            System.out.println("No nearby users found for user: " + userId);
            throw new NoNearbyUsersException(userId);
        }

        System.out.println("Nearby users for user " + userId + ": " + nearbyResult.getNearbyUserIds());
        
        // ✅ return result so controller can use it
        return nearbyResult;
    }
}