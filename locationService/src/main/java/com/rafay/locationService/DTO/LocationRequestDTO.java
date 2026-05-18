package com.rafay.locationService.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class LocationRequestDTO {
    private String userId;
    private BigDecimal latitude;
    private BigDecimal longitude;
}