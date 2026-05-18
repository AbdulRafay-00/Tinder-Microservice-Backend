package com.rafay.user_service.dto.swipLogicDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SwipEventKafkaProduce {
    private String swiperId;
    private String swipedId;
    private String swipeDirection;
}
