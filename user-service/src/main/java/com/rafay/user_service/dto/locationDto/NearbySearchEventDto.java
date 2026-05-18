package com.rafay.user_service.dto.locationDto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NearbySearchEventDto {

    private String userId;
    private BigDecimal latitude;
    private BigDecimal longitude;
}