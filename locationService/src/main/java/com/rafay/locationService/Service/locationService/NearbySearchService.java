package com.rafay.locationService.Service.locationService;

import com.rafay.locationService.DTO.LocationRequestDTO;
import com.rafay.locationService.DTO.NearbySearchResultDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class NearbySearchService {

    @Autowired
    private LocationService locationService;

    public NearbySearchResultDto processNearbySearch(
            String userId,
            BigDecimal latitude,
            BigDecimal longitude) {

        List<String> nearbyUserIds = locationService.getRecommendations(
            userId,
            latitude,
            longitude
        );

        return new NearbySearchResultDto(userId, nearbyUserIds);
    }

    public NearbySearchResultDto processNearbySearch(LocationRequestDTO request) {
        return processNearbySearch(
            request.getUserId(),
            request.getLatitude(),
            request.getLongitude()
        );
    }

    public List<String> processNearbySearchAsList(LocationRequestDTO request) {
        return processNearbySearch(request).getNearbyUserIds();
    }
}