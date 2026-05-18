package com.rafay.user_service.db_entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

import jakarta.persistence.CascadeType;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
@Entity
public class LiveLocationDB {

    @Id
    private String userId;

    @NonNull
    @Column(nullable = false, precision = 9, scale = 6)
    private BigDecimal latitude;

    @NonNull
    @Column(nullable = false, precision = 9, scale = 6)
    private BigDecimal longitude;

    @OneToOne
    @MapsId
    @JoinColumn(name = "userId", referencedColumnName = "userId")
    private AuthCredentials authCredentials;
}