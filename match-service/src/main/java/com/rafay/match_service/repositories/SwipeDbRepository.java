package com.rafay.match_service.repositories;

import com.rafay.match_service.db_entries.Swiptable.SwipeDB;
import com.rafay.match_service.db_entries.Swiptable.SwipeIdEmbedd;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SwipeDbRepository extends JpaRepository<SwipeDB, SwipeIdEmbedd> {

    // Check if the OTHER person already swiped ME right
    boolean existsByIdSwiperIdAndIdSwipedId(String swiperId, String swipedId);
}