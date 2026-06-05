package com.rafay.Orchestration_Service.DTO;

import java.math.BigDecimal;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LocationRequset {
    private BigDecimal latitude;
    private BigDecimal longitude;
}
