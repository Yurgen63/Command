package org.skypro.star.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.UUID;

public record RecommendationResponse(
        @JsonProperty("user_id")
        UUID userId,

        List<RecommendationDto> recommendations
) {
}
