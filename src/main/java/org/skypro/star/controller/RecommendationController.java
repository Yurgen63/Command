package org.skypro.star.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.skypro.star.dto.RecommendationDto;
import org.skypro.star.dto.RecommendationResponse;
import org.skypro.star.service.RecommendationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@Tag(name = "Рекомендации", description = "API для получения рекомендаций банковских продуктов")
public class RecommendationController {

    private final RecommendationService service;

    public RecommendationController(RecommendationService service) {
        this.service = service;
    }

    @Operation(
            summary = "Получить рекомендации для пользователя",
            description = "Возвращает список продуктов, которые подходят клиенту на основе его транзакций."
    )
    @ApiResponse(responseCode = "200", description = "Успешный ответ со списком рекомендаций")

    @GetMapping("/recommendation/{userId}")
    public RecommendationResponse getRecommendations(
            @Parameter(description = "ID пользователя в формате UUID", required = true, example = "cd515076-5d8a-44be-930e-8d4fcb79f42d")
            @PathVariable UUID userId) {
        List<RecommendationDto> recommendations = service.getRecommendations(userId);
        return new RecommendationResponse(userId, recommendations);
    }
}