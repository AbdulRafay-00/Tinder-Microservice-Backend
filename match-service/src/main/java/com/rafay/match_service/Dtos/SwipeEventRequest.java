package com.rafay.match_service.Dtos;

import com.rafay.match_service.db_entries.Swiptable.SwipeDirectionEnum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SwipeEventRequest {
    private String swiperId;
    private String swipedId;
    private String swipeDirection;
}