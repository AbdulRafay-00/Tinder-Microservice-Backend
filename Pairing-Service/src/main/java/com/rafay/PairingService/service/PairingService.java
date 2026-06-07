package com.rafay.PairingService.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rafay.PairingService.DB.PairDB;
import com.rafay.PairingService.DB.PairDbId;
import com.rafay.PairingService.DB.PairEnum;
import com.rafay.PairingService.repository.PairDBRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PairingService {

    private final PairDBRepository pairDBRepository;
    @Transactional
    public void savePairingEvent(String swiperId, String swipedId) {
        PairDbId id = new PairDbId(swiperId, swipedId);
        PairDB pairDB = new PairDB(id, LocalDateTime.now(), PairEnum.PENDING);
        pairDBRepository.save(pairDB);
    }
}