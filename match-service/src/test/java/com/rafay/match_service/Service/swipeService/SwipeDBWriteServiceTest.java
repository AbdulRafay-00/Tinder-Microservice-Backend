package com.rafay.match_service.Service.swipeService;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.rafay.match_service.Dtos.SwipeEventRequest;
import com.rafay.match_service.Kafka.PairingEventProducer.PairingEventProducer;
import com.rafay.match_service.db_entries.Swiptable.SwipeDB;
import com.rafay.match_service.db_entries.Swiptable.SwipeDirectionEnum;
import com.rafay.match_service.repositories.SwipeDbRepository;

@ExtendWith(MockitoExtension.class)
public class SwipeDBWriteServiceTest {

    @Mock
    private SwipeDbRepository swipeDbRepository;
    @Mock
    private PairingEventProducer pairingEventProducer;
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

    @Test
    void testPersistSwipe_SwipeDoesNotExist_RIGHT_Swipe_NoMatch() {
        SwipeEventRequest request = new SwipeEventRequest(
                "swipedId123",
                "RIGHT");
        String swiperId = "swiperId123";
        SwipeDirectionEnum direction = SwipeDirectionEnum.RIGHT;

        when(swipeDbRepository.existsByIdSwiperIdAndIdSwipedId(swiperId, request.getSwipedId()))
                .thenReturn(false);

        when(swipeDbRepository.existsByIdSwiperIdAndIdSwipedId(request.getSwipedId(), swiperId))
                .thenReturn(false);
        swipeDBWriteService.persistSwipe(request, swiperId, direction);

        // 1. First check — has the user already swiped this person?
        verify(swipeDbRepository, times(1))
                .existsByIdSwiperIdAndIdSwipedId(swiperId, request.getSwipedId());

        // 2. Swipe got saved
        verify(swipeDbRepository, times(1)).save(any(SwipeDB.class));

        // 3. Second check — match-detection call (swapped args)
        verify(swipeDbRepository, times(1))
                .existsByIdSwiperIdAndIdSwipedId(request.getSwipedId(), swiperId);

        // 4. No match found, so pairing event must never fire
        verify(pairingEventProducer, never()).publishPairingEvent(any(), any());

    }

    @Test
    void testPersistSwipe_SwipeDoesNotExist_RIGHT_Swipe_MatchFound() {
        SwipeEventRequest request = new SwipeEventRequest(
                "swipedId123",
                "RIGHT");
        String swiperId = "swiperId123";
        SwipeDirectionEnum direction = SwipeDirectionEnum.RIGHT;

        when(swipeDbRepository.existsByIdSwiperIdAndIdSwipedId(swiperId, request.getSwipedId()))
                .thenReturn(false);

        when(swipeDbRepository.existsByIdSwiperIdAndIdSwipedId(request.getSwipedId(), swiperId))
                .thenReturn(true);

        swipeDBWriteService.persistSwipe(request, swiperId, direction);

        // 1. First duplicate-check call
        verify(swipeDbRepository, times(1))
                .existsByIdSwiperIdAndIdSwipedId(swiperId, request.getSwipedId());

        // 2. Swipe got saved
        verify(swipeDbRepository, times(1)).save(any(SwipeDB.class));

        // 3. Second check — match-detection call (swapped args)
        verify(swipeDbRepository, times(1))
                .existsByIdSwiperIdAndIdSwipedId(request.getSwipedId(), swiperId);

        // 4. Match found — pairing event MUST fire
        verify(pairingEventProducer, times(1)).publishPairingEvent(swiperId, request.getSwipedId());
    }

}
