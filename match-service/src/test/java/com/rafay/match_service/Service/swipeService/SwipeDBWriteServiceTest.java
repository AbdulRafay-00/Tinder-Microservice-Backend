package com.rafay.match_service.Service.swipeService;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.rafay.match_service.Dtos.SwipeEventRequest;
import com.rafay.match_service.db_entries.Swiptable.SwipeDB;
import com.rafay.match_service.db_entries.Swiptable.SwipeDirectionEnum;
import com.rafay.match_service.repositories.SwipeDbRepository;

@ExtendWith(MockitoExtension.class)
public class SwipeDBWriteServiceTest {

    @Mock
    private SwipeDbRepository swipeDbRepository;
    @Mock
    private SwipeDB swipeDB;
    @InjectMocks
    private SwipeDBWriteService swipeDBWriteService;

    @Test
    void testPersistSwipe_SwipeExists() {
        SwipeEventRequest request = new SwipeEventRequest(
                "swipedId123",
                "RIGHT");
        String swiperId = "swiperId123";
        SwipeDirectionEnum direction = SwipeDirectionEnum.RIGHT;

        when(swipeDbRepository.existsByIdSwiperIdAndIdSwipedId(swiperId, request.getSwipedId()))
                .thenReturn(true);

        swipeDBWriteService.persistSwipe(request, swiperId, direction);

        verify(swipeDbRepository, times(1)).existsByIdSwiperIdAndIdSwipedId(swiperId, request.getSwipedId());

    }

    @Test
    void testPersistSwipe_SwipeDoesNotExist_LEFT_Swipe() {
        SwipeEventRequest request = new SwipeEventRequest(
                "swipedId456",
                "LEFT");
        String swiperId = "swiperId456";
        SwipeDirectionEnum direction = SwipeDirectionEnum.LEFT;

        when(swipeDbRepository.existsByIdSwiperIdAndIdSwipedId(swiperId, request.getSwipedId()))
                .thenReturn(false);

        swipeDBWriteService.persistSwipe(request, swiperId, direction);

        verify(swipeDbRepository, times(1)).save(any(SwipeDB.class));
    }
}
