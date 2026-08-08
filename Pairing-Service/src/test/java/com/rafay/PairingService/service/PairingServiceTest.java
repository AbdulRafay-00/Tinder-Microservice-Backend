package com.rafay.PairingService.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.rafay.PairingService.DB.PairDB;
import com.rafay.PairingService.kafka.notifyemailproducer.PairingEventProducer;
import com.rafay.PairingService.repository.PairDBRepository;

@ExtendWith(MockitoExtension.class)
public class PairingServiceTest {

    @Mock
    private PairDBRepository pairDBRepository;
    @Mock
    private PairingEventProducer pairingEventProducer;

    @InjectMocks
    private PairingService pairingService;

    @Test
    void testSavePairingEvent() {
        // Assert
        String swiperId = "swiper123";
        String swipedId = "swiped456";
        // when(pairDBRepository.save(any(PairDB.class))).thenAnswer(invocation ->
        // invocation.getArgument(0));
        when(pairDBRepository.save(any(PairDB.class))).thenReturn(new PairDB());
        // Act
        pairingService.savePairingEvent(swiperId, swipedId);

        //
        verify(pairDBRepository, times(1)).save(any(PairDB.class));
        verify(pairingEventProducer, times(1)).sendPairingEvent(swiperId, swipedId);

    }

    @Test
    void testSavePairingEvent_WhenRepositoryThrowsException() {
        // Arrange
        String swiperId = "swiper123";
        String swipedId = "swiped456";

        when(pairDBRepository.save(any(PairDB.class)))
                .thenThrow(new RuntimeException("Database Error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            pairingService.savePairingEvent(swiperId, swipedId);
        });

        // Verify that save was attempted
        verify(pairDBRepository, times(1)).save(any(PairDB.class));

        // Verify that Kafka event was NOT sent
        verify(pairingEventProducer, never())
                .sendPairingEvent(anyString(), anyString());
    }
}
