package org.skypro.star.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.skypro.star.dto.RecommendationDto;
import org.skypro.star.rules.RecommendationRuleSet;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {

    @Mock
    RecommendationRuleSet rule1;
    @Mock
    RecommendationRuleSet rule2;
    @Mock RecommendationRuleSet rule3;

    private RecommendationServiceTest service;

    @BeforeEach
    void setUp() {
        service = new RecommendationServiceTest(List.of(rule1, rule2, rule3));
    }

    @Test
    void returnsOnlyNonEmptyRecommendations() {
        UUID userId = UUID.randomUUID();
        RecommendationDto dto = new RecommendationDto("Invest 500", UUID.randomUUID(), "text");

        when(rule1.check(userId)).thenReturn(Optional.of(dto));
        when(rule2.check(userId)).thenReturn(Optional.empty());
        when(rule3.check(userId)).thenReturn(Optional.empty());

        List<RecommendationDto> result = service.getRecommendations(userId);

        assertEquals(List.of(dto), result);
        verify(rule1).check(userId);
        verify(rule2).check(userId);
        verify(rule3).check(userId);  // важно: вызываются ВСЕ правила
    }

    @Test
    void returnsEmptyListWhenNoRulesMatch() {
        UUID userId = UUID.randomUUID();
        when(rule1.check(userId)).thenReturn(Optional.empty());
        when(rule2.check(userId)).thenReturn(Optional.empty());
        when(rule3.check(userId)).thenReturn(Optional.empty());

        assertTrue(service.getRecommendations(userId).isEmpty());
    }

    @Test
    void collectsMultipleRecommendations() {
        UUID userId = UUID.randomUUID();
        RecommendationDto a = new RecommendationDto("A", UUID.randomUUID(), "a");
        RecommendationDto b = new RecommendationDto("B", UUID.randomUUID(), "b");

        when(rule1.check(userId)).thenReturn(Optional.of(a));
        when(rule2.check(userId)).thenReturn(Optional.empty());
        when(rule3.check(userId)).thenReturn(Optional.of(b));

        assertEquals(List.of(a, b), service.getRecommendations(userId));
    }
}