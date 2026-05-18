package com.rafay.user_service.dto.locationDto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NearbySearchRequestDto {

    @NotNull(message = "latitude is required")
    private BigDecimal latitude;

    @NotNull(message = "longitude is required")
    private BigDecimal longitude;
}