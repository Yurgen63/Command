package org.skypro.star.service;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.skypro.star.dto.RecommendationDto;
import org.skypro.star.dto.rule.RuleDto;
import org.skypro.star.entity.RuleEntity;
import org.skypro.star.mapper.RuleMapper;
import org.skypro.star.repository.RuleRepository;
import org.skypro.star.rules.DynamicRuleChecker;
import org.skypro.star.rules.RecommendationRuleSet;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class RecommendationServiceTest {

    private final RuleRepository ruleRepository = Mockito.mock(RuleRepository.class);
    private final RuleMapper ruleMapper = Mockito.mock(RuleMapper.class);
    private final DynamicRuleChecker dynamicRuleChecker = Mockito.mock(DynamicRuleChecker.class);

    @Test
    void returnsFixedRuleRecommendations() {
        UUID userId = UUID.randomUUID();
        RecommendationDto dto = new RecommendationDto("Test", UUID.randomUUID(), "text");

        RecommendationRuleSet fixedRule = Mockito.mock(RecommendationRuleSet.class);
        when(fixedRule.check(userId)).thenReturn(Optional.of(dto));
        when(ruleRepository.findAllWithQueries()).thenReturn(List.of());

        RecommendationService service = new RecommendationService(
                List.of(fixedRule),
                ruleRepository,
                ruleMapper,
                dynamicRuleChecker
        );

        List<RecommendationDto> result = service.getRecommendations(userId);

        assertEquals(1, result.size());
        assertEquals("Test", result.get(0).name());
    }

    @Test
    void combinesFixedAndDynamicRecommendations() {
        UUID userId = UUID.randomUUID();
        RecommendationDto fixed = new RecommendationDto("Fixed", UUID.randomUUID(), "text");
        RecommendationDto dynamic = new RecommendationDto("Dynamic", UUID.randomUUID(), "text");

        RecommendationRuleSet fixedRule = Mockito.mock(RecommendationRuleSet.class);
        when(fixedRule.check(userId)).thenReturn(Optional.of(fixed));

        RuleEntity entity = Mockito.mock(RuleEntity.class);
        RuleDto ruleDto = new RuleDto(1L, "Dynamic", UUID.randomUUID(), "text", List.of());

        when(ruleRepository.findAllWithQueries()).thenReturn(List.of(entity));
        when(ruleMapper.toDto(entity)).thenReturn(ruleDto);
        when(dynamicRuleChecker.check(userId, ruleDto)).thenReturn(Optional.of(dynamic));

        RecommendationService service = new RecommendationService(
                List.of(fixedRule), ruleRepository, ruleMapper, dynamicRuleChecker);

        List<RecommendationDto> result = service.getRecommendations(userId);

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(d -> d.name().equals("Fixed")));
        assertTrue(result.stream().anyMatch(d -> d.name().equals("Dynamic")));
    }
}