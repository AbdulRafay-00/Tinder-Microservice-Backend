package com.rafay.PairingService.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rafay.PairingService.DB.PairDB;
import com.rafay.PairingService.DB.PairDbId;
import com.rafay.PairingService.DB.PairEnum;
import com.rafay.PairingService.kafka.notifyemailproducer.PairingEventProducer;
import com.rafay.PairingService.repository.PairDBRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PairingService {

    private final PairDBRepository pairDBRepository;
    private final PairingEventProducer pairingEventProducer;

    @Transactional
    public void savePairingEvent(String swiperId, String swipedId) {
        PairDbId id = new PairDbId(swiperId, swipedId);
        PairDB pairDB = new PairDB(id, LocalDateTime.now(), PairEnum.PENDING);
        pairDBRepository.save(pairDB);
        // Send pairing event to Kafka topic
        pairingEventProducer.sendPairingEvent(swiperId, swipedId);
    }
}