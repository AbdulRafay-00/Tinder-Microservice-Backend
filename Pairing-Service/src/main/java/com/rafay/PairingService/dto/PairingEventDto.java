package com.rafay.PairingService.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PairingEventDto(String swiperId, String swipedId) {
}