package org.skypro.star.dto.rule;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.UUID;

public record RuleDto(
        Long id,

        @JsonProperty("product_name")
        String productName,

        @JsonProperty("product_id")
        UUID productId,

        @JsonProperty("product_text")
        String productText,

        List<QueryDto> rule
) {
}