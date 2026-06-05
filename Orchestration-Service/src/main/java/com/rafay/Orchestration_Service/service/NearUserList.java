package com.rafay.Orchestration_Service.service;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.rafay.Orchestration_Service.DTO.LocationRequset;
import com.rafay.Orchestration_Service.DTO.NearLocationRequestDTO;
import com.rafay.Orchestration_Service.DTO.NearbySearchResultDto;
import com.rafay.Orchestration_Service.Redis.NearByUserCache;
import com.rafay.Orchestration_Service.FeignClients.NearByUserEvent.NearByReq;
import com.rafay.Orchestration_Service.FeignClients.NearByUserEvent.FilteredList;
import com.rafay.Orchestration_Service.GlobalExeption.NoNearbyUsersException;

@Service
public class NearUserList {

    private final NearByReq nearByReq;
    private final FilteredList filteredList;
    private final NearByUserCache nearByUserCache;

    public NearUserList(NearByReq nearByReq, FilteredList filteredList, NearByUserCache nearByUserCache) {
        this.nearByReq = nearByReq;
        this.filteredList = filteredList;
        this.nearByUserCache = nearByUserCache;
    }

    public NearbySearchResultDto getNearUserList(LocationRequset locationRequest, String userId) {

        //  — check cache first
        List<String> cachedResult = nearByUserCache.getCachedNearbyUsers(userId);
        if (cachedResult != null && !cachedResult.isEmpty()) {
            System.out.println("✅ Cache HIT for: " + userId);
            return new NearbySearchResultDto(userId, cachedResult);
        }

        System.out.println("❌ Cache MISS for: " + userId);

        // — call Location Service
        NearLocationRequestDTO nearLocationRequestDTO = new NearLocationRequestDTO();
        nearLocationRequestDTO.setUserId(userId);
        nearLocationRequestDTO.setLatitude(locationRequest.getLatitude());
        nearLocationRequestDTO.setLongitude(locationRequest.getLongitude());

        ResponseEntity<NearbySearchResultDto> nearbySearchResponse = nearByReq.getNearbySearchSync(nearLocationRequestDTO);
        NearbySearchResultDto nearbyResult = nearbySearchResponse == null ? null : nearbySearchResponse.getBody();

        if (nearbyResult == null || nearbyResult.getNearbyUserIds() == null || nearbyResult.getNearbyUserIds().isEmpty()) {
            throw new NoNearbyUsersException(userId);
        }

        //  — call Match Service
        ResponseEntity<NearbySearchResultDto> filteredResponse = filteredList.acceptNearbyUsers(nearbyResult);
        NearbySearchResultDto filteredResult = filteredResponse == null ? null : filteredResponse.getBody();

        if (filteredResult == null || filteredResult.getNearbyUserIds().isEmpty()) {
            throw new NoNearbyUsersException(userId);
        }

        // — store in Redis
        nearByUserCache.cacheNearbyUsers(userId, filteredResult.getNearbyUserIds());
        System.out.println("✅ Cached " + filteredResult.getNearbyUserIds().size() + " users for: " + userId);

        return filteredResult;
    }
}