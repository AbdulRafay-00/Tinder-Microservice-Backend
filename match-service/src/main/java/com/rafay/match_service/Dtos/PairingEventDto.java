package com.rafay.match_service.Dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PairingEventDto {
    private String swiperId;
    private String swipedId;
}