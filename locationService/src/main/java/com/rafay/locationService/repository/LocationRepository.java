package com.rafay.locationService.repository;

import com.rafay.locationService.db_entries.LiveLocationDB;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationRepository extends JpaRepository<LiveLocationDB, String> {
}