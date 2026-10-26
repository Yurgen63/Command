package org.skypro.star.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.skypro.star.dto.rule.RuleDto;
import org.skypro.star.dto.rule.RuleListResponse;
import org.skypro.star.service.RuleService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rule")
@Tag(name = "Правила рекомендаций", description = "API для управления динамическими правилами")
public class RuleController {

    private final RuleService service;

    public RuleController(RuleService service) {
        this.service = service;
    }

    @Operation(summary = "Создать новое правило рекомендаций")
    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public RuleDto create(@RequestBody RuleDto dto) {
        return service.create(dto);
    }

    @Operation(summary = "Получить список всех правил")
    @GetMapping
    public RuleListResponse findAll() {
        return new RuleListResponse(service.findAll());
    }

    @Operation(summary = "Удалить правило по id")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}