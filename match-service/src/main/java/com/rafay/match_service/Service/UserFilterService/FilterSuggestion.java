package com.rafay.match_service.Service.UserFilterService;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.rafay.match_service.Dtos.NearbySearchResultDto;
import com.rafay.match_service.repositories.SwipeDbRepository;

@Service
public class FilterSuggestion {
    
    private  SwipeDbRepository swipeDBRepository;

    public FilterSuggestion(SwipeDbRepository swipeDBRepository) {
        this.swipeDBRepository = swipeDBRepository;
    }

    public NearbySearchResultDto filterSwiped(NearbySearchResultDto request) {

        // Step 1 — get all users this person already swiped
        List<String> alreadySwiped = swipeDBRepository
                .findSwipedIdsBySwiperId(request.getUserId());

        // Step 2 — copy list (don't modify original)
        List<String> filteredList = new ArrayList<>(request.getNearbyUserIds());

        // Step 3 — remove already swiped
        filteredList.removeAll(alreadySwiped);

        // Step 4 — return filtered result
        return new NearbySearchResultDto(request.getUserId(), filteredList);
    }
}
