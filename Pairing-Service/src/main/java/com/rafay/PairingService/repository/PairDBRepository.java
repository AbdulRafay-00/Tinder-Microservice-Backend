package com.rafay.PairingService.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rafay.PairingService.DB.PairDbId;
import com.rafay.PairingService.DB.PairDB;

@Repository
public interface PairDBRepository extends JpaRepository<PairDB, PairDbId> {
}