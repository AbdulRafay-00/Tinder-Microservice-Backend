package com.rafay.match_service.Service.swipeService;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.rafay.match_service.Dtos.SwipeEventRequest;
import com.rafay.match_service.db_entries.Swiptable.SwipeDirectionEnum;

@ExtendWith(MockitoExtension.class)
public class SwipeServiceTest {

    @Mock
    private SwipeDBWriteService swipePersistenceService;

    @InjectMocks
    private SwipeService swipeService;

    @Test
    void testSaveSwipeEvent_CallsPersistSwipeWithCorrectArgs() {
        SwipeEventRequest request = new SwipeEventRequest(
                "swipedId123",
                "RIGHT");
        String swiperId = "swiperId123";

        swipeService.saveSwipeEvent(request, swiperId);

        verify(swipePersistenceService, times(1))
                .persistSwipe(request, swiperId, SwipeDirectionEnum.RIGHT);
    }
}