package com.rafay.match_service.repositories;

import com.rafay.match_service.db_entries.Swiptable.SwipeDB;
import com.rafay.match_service.db_entries.Swiptable.SwipeIdEmbedd;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SwipeDbRepository extends JpaRepository<SwipeDB, SwipeIdEmbedd> {

    // Check if the OTHER person already swiped ME right
    boolean existsByIdSwiperIdAndIdSwipedId(String swiperId, String swipedId);
    
    @Query("SELECT s.id.swipedId FROM SwipeDB s WHERE s.id.swiperId = :swiperId")
    List<String> findSwipedIdsBySwiperId(@Param("swiperId") String swiperId);
}