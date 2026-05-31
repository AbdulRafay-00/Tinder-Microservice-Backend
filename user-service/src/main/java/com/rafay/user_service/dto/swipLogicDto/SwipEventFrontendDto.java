package com.rafay.user_service.dto.swipLogicDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SwipEventFrontendDto {
    private String swipedId;
    private SwipeDirection swipeDirection;
}
