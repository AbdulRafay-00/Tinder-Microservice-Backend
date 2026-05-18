package com.rafay.user_service.dto.locationDto;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LocataionDto {
    private String userId;
    private double latitude;
    private double longitude;
}
