package com.rafay.match_service.Dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NearbySearchResultDto {

    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("nearby_user_ids")
    private List<String> nearbyUserIds;
}