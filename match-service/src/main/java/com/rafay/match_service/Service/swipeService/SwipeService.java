package com.rafay.match_service.Service.swipeService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.rafay.match_service.Dtos.SwipeEventRequest;
import com.rafay.match_service.db_entries.Swiptable.SwipeDirectionEnum;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SwipeService {

    private static final Logger log = LoggerFactory.getLogger(SwipeService.class);
    private final SwipeDBWriteService swipePersistenceService;

    @Async
    public void saveSwipeEvent(SwipeEventRequest request, String swiperId) {
        SwipeDirectionEnum direction = SwipeDirectionEnum.valueOf(
            request.getSwipeDirection().toUpperCase()
        );
        log.info("Swipe received — swiper: {}, swiped: {}, direction: {}",
            swiperId, request.getSwipedId(), direction);

        swipePersistenceService.persistSwipe(request, swiperId, direction);
    }
}