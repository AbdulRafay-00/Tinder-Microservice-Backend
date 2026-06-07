package com.rafay.match_service.Service.swipeService;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rafay.match_service.Dtos.SwipeEventRequest;
import com.rafay.match_service.db_entries.Swiptable.SwipeDB;
import com.rafay.match_service.db_entries.Swiptable.SwipeDirectionEnum;
import com.rafay.match_service.db_entries.Swiptable.SwipeIdEmbedd;
import com.rafay.match_service.repositories.SwipeDbRepository;

import lombok.RequiredArgsConstructor;



@Service
@RequiredArgsConstructor
public class SwipeDBWriteService {

    private static final Logger log = LoggerFactory.getLogger(SwipeDBWriteService.class);
    private final SwipeDbRepository swipeDbRepository;

/*  why did we create twofunction because @Async create a new thread and
@Transactional does not work across threads. So we need to call the 
@Transactional function from the same thread so first we craete the 
thread with async and from that function we call function that opens 
tranaction for the current new thread so it can work fine and over 
orignal thread can be return without waiting*/
    @Transactional
    public void persistSwipe(SwipeEventRequest request, String swiperId, SwipeDirectionEnum direction) {

        boolean alreadySwiped = swipeDbRepository.existsByIdSwiperIdAndIdSwipedId(
            swiperId, request.getSwipedId()
        );
        if (alreadySwiped) {
            log.warn("Duplicate swipe ignored — swiper: {}, swiped: {}", swiperId, request.getSwipedId());
            return;
        }

        SwipeDB swipe = new SwipeDB(
            new SwipeIdEmbedd(swiperId, request.getSwipedId()),
            direction,
            LocalDateTime.now()
        );
        swipeDbRepository.save(swipe);
        log.info("Swipe saved — swiper: {}, swiped: {}, direction: {}", swiperId, request.getSwipedId(), direction);

        if (direction == SwipeDirectionEnum.LEFT) {
            return;
        }

        boolean isMatch = swipeDbRepository.existsByIdSwiperIdAndIdSwipedId(
            request.getSwipedId(), swiperId
        );

        if (isMatch) {
            log.info("MATCH detected — {} & {}", swiperId, request.getSwipedId());
            // TODO: fire Kafka event
        } else {
            log.info("No match yet — waiting for other person to swipe");
        }
    }
}