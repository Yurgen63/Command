package org.skypro.star.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record RecommendationDto(
        @Schema(description = "Название продукта", example = "Invest 500")
        String name,
        @Schema(description = "Уникальный идентификатор продукта", example = "147f6a0f-3b91-413b-ab99-87f081d60d5a")
        UUID id,
        @Schema(description = "Текстовое описание продукта")
        String text
) {}
