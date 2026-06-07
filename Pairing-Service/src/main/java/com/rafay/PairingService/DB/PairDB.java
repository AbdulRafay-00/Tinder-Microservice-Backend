package com.rafay.PairingService.DB;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
//@AllArgsConstructor
@Data
@Table(name = "pair_db")
@Entity
public class PairDB {
    public PairDB(PairDbId pairDbId, LocalDateTime time, PairEnum status) {
        this.pairDbId = pairDbId;
        this.time = time;
        this.status = status;
    }

    @EmbeddedId
    private PairDbId pairDbId;

    @Column(name = "time", nullable = false)
    private LocalDateTime time;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private PairEnum status;
}