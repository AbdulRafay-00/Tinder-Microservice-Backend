package com.rafay.Orchestration_Service.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class NearLocationRequestDTO {
    private String userId;
    private BigDecimal latitude;
    private BigDecimal longitude;
}