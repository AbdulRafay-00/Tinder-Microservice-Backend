package com.rafay.locationService.db_entries;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "live_locationdb")
public class LiveLocationDB {

    @Id
    private String userId;

    @NonNull
    @Column(nullable = false, precision = 9, scale = 6)
    private BigDecimal latitude;

    @NonNull
    @Column(nullable = false, precision = 9, scale = 6)
    private BigDecimal longitude;

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
}