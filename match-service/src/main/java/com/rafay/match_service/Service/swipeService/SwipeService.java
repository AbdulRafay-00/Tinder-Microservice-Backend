package com.rafay.match_service.Service.swipeService;

import java.time.LocalDateTime;

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
public class SwipeService {

    private final SwipeDbRepository swipeDbRepository;

    @Transactional
    public void saveSwipeEvent(SwipeEventRequest request) {

        SwipeDirectionEnum direction = SwipeDirectionEnum.valueOf(
            request.getSwipeDirection().toUpperCase()
        );

        // Step 1 — ignore LEFT swipes
        if (direction == SwipeDirectionEnum.LEFT) {
            System.out.println("LEFT swipe — ignored");
            return;
        }


        boolean alreadySwiped = swipeDbRepository.existsByIdSwiperIdAndIdSwipedId(
        request.getSwiperId(),
        request.getSwipedId()
    );
    if (alreadySwiped) {
        System.out.println("Duplicate swipe — ignored");
        return;
    }

        // Step 2 — save this RIGHT swipe to DB
        SwipeDB swipe = new SwipeDB(
            new SwipeIdEmbedd(request.getSwiperId(), request.getSwipedId()),
            direction,
            LocalDateTime.now()
        );
        swipeDbRepository.save(swipe);
        System.out.println("Swipe saved: " + request.getSwiperId() + " → " + request.getSwipedId());

        // Step 3 — check if other person already swiped me RIGHT
        boolean isMatch = swipeDbRepository.existsByIdSwiperIdAndIdSwipedId(
            request.getSwipedId(), // they are the swiper
            request.getSwiperId()  // I am the swiped
        );

        // Step 4 — if match, save to match table
        if (isMatch) {
            System.out.println("🎉 MATCH: " + request.getSwiperId() + " & " + request.getSwipedId());
            // TODO: save to Match table (next step)
        } else {
            System.out.println("No match yet — waiting for other person to swipe");
        }
    }
}