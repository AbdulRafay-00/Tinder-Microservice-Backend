package com.rafay.match_service.Service.UserFilterService;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.rafay.match_service.Dtos.NearbySearchResultDto;
import com.rafay.match_service.repositories.SwipeDbRepository;

@ExtendWith(MockitoExtension.class)
public class FilterSuggestionTest {

    @Mock
    private SwipeDbRepository swipeDBRepository;

    @InjectMocks
    private FilterSuggestion filterSuggestion;

    @Test
    void testFilterSwiped_RemovesAlreadySwipedUsers() {
        String userId = "userId123";
        List<String> nearbyUserIds = Arrays.asList("user1", "user2", "user3");
        NearbySearchResultDto request = new NearbySearchResultDto(userId, nearbyUserIds);

        List<String> alreadySwiped = Arrays.asList("user2");

        when(swipeDBRepository.findSwipedIdsBySwiperId(userId)).thenReturn(alreadySwiped);

        NearbySearchResultDto result = filterSuggestion.filterSwiped(request);

        assertEquals(userId, result.getUserId());
        assertEquals(Arrays.asList("user1", "user3"), result.getNearbyUserIds());
        verify(swipeDBRepository, times(1)).findSwipedIdsBySwiperId(userId);
    }

    @Test
    void testFilterSwiped_NoOneSwipedYet_ReturnsFullList() {
        String userId = "userId123";
        List<String> nearbyUserIds = Arrays.asList("user1", "user2", "user3");
        NearbySearchResultDto request = new NearbySearchResultDto(userId, nearbyUserIds);

        when(swipeDBRepository.findSwipedIdsBySwiperId(userId)).thenReturn(List.of());

        NearbySearchResultDto result = filterSuggestion.filterSwiped(request);

        assertEquals(nearbyUserIds, result.getNearbyUserIds());
    }

    @Test
    void testFilterSwiped_DoesNotMutateOriginalList() {
        String userId = "userId123";
        List<String> nearbyUserIds = Arrays.asList("user1", "user2", "user3");
        NearbySearchResultDto request = new NearbySearchResultDto(userId, nearbyUserIds);

        when(swipeDBRepository.findSwipedIdsBySwiperId(userId)).thenReturn(Arrays.asList("user2"));

        filterSuggestion.filterSwiped(request);

        // original list on the request object should remain untouched
        assertEquals(Arrays.asList("user1", "user2", "user3"), request.getNearbyUserIds());
    }
}