package org.skypro.star.service;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.skypro.star.dto.RecommendationDto;
import org.skypro.star.mapper.RuleMapper;
import org.skypro.star.repository.RuleRepository;
import org.skypro.star.rules.DynamicRuleChecker;
import org.skypro.star.rules.RecommendationRuleSet;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        when(ruleRepository.findAll()).thenReturn(List.of());

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
}