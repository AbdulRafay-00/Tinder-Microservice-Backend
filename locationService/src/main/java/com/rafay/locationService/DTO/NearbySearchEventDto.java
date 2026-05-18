package com.rafay.locationService.DTO;


import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NearbySearchEventDto {
    private String userId;
    private BigDecimal latitude;
    private BigDecimal longitude;
}