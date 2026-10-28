package org.skypro.star.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.skypro.star.repository.RecommendationsRepository;
import org.springframework.boot.info.BuildProperties;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/management")
@Tag(name = "Управление сервисом", description = "Служебные эндпоинты: сброс кешей, информация о сервисе")
public class ManagementController {

    private final RecommendationsRepository recommendationsRepository;
    private final BuildProperties buildProperties;

    public ManagementController(RecommendationsRepository recommendationsRepository,
                                BuildProperties buildProperties) {
        this.recommendationsRepository = recommendationsRepository;
        this.buildProperties = buildProperties;
    }

    @Operation(
            summary = "Сбросить все кеши рекомендаций",
            description = "Очищает кеши countCache, sumCache, statsCache. " +
                    "Следующие запросы рекомендаций пойдут в БД."
    )
    @ApiResponse(responseCode = "200", description = "Кеши очищены")
    @PostMapping("/clear-caches")
    @ResponseStatus(HttpStatus.OK)
    public void clearCaches() {
        recommendationsRepository.clearCaches();
    }

    @Operation(
            summary = "Информация о сервисе",
            description = "Возвращает название и версию сервиса из pom.xml."
    )
    @ApiResponse(responseCode = "200", description = "Информация о сервисе")
    @GetMapping("/info")
    public Map<String, String> info() {
        return Map.of(
                "name", buildProperties.getArtifact(),
                "version", buildProperties.getVersion()
        );
    }
}