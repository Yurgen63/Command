package org.skypro.star.rules;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.skypro.star.dto.RecommendationDto;
import org.skypro.star.dto.rule.QueryDto;
import org.skypro.star.dto.rule.RuleDto;
import org.skypro.star.repository.RuleStatsRepository;
import org.skypro.star.rules.query.RuleQuery;
import org.skypro.star.rules.query.RuleQueryFactory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DynamicRuleCheckerTest {

    @Mock RuleQueryFactory factory;
    @Mock RuleQuery query;
    @Mock RuleStatsRepository statsRepository;

    private DynamicRuleChecker checker;

    @BeforeEach
    void setUp() {
        checker = new DynamicRuleChecker(factory, statsRepository);
    }

    private RuleDto rule(Long id, QueryDto... queries) {
        return new RuleDto(id, "Product", UUID.randomUUID(), "text", List.of(queries));
    }

    @Test
    void returnsRecommendationWhenAllQueriesPass() {
        UUID userId = UUID.randomUUID();
        QueryDto q = new QueryDto("USER_OF", List.of("DEBIT"), false);
        RuleDto dto = rule(1L, q);

        when(factory.getQuery(eq("USER_OF"), anyList())).thenReturn(query);
        when(query.evaluate(eq(userId), anyList())).thenReturn(true);

        Optional<RecommendationDto> result = checker.check(userId, dto);

        assertTrue(result.isPresent());
        assertEquals("Product", result.get().name());
        verify(statsRepository, times(1)).increment(1L);
    }

    @Test
    void returnsEmptyWhenAnyQueryFails() {
        UUID userId = UUID.randomUUID();
        QueryDto ok = new QueryDto("USER_OF", List.of("DEBIT"), false);
        QueryDto fail = new QueryDto("ACTIVE_USER_OF", List.of("CREDIT"), false);
        RuleDto dto = rule(2L, ok, fail);

        when(factory.getQuery(anyString(), anyList())).thenReturn(query);
        when(query.evaluate(eq(userId), anyList()))
                .thenReturn(true)    // первый запрос пройдёт
                .thenReturn(false);  // второй — нет

        Optional<RecommendationDto> result = checker.check(userId, dto);

        assertTrue(result.isEmpty());
        verify(statsRepository, never()).increment(any());
    }

    @Test
    void appliesNegateFlag() {
        UUID userId = UUID.randomUUID();
        QueryDto negated = new QueryDto("USER_OF", List.of("INVEST"), true);
        RuleDto dto = rule(3L, negated);

        when(factory.getQuery(eq("USER_OF"), anyList())).thenReturn(query);
        when(query.evaluate(eq(userId), anyList())).thenReturn(false);

        Optional<RecommendationDto> result = checker.check(userId, dto);

        assertTrue(result.isPresent());
        verify(statsRepository, times(1)).increment(3L);
    }

    @Test
    void doesNotIncrementStatsWhenRuleHasNoId() {
        UUID userId = UUID.randomUUID();
        QueryDto q = new QueryDto("USER_OF", List.of("DEBIT"), false);
        RuleDto dto = rule(null, q);

        when(factory.getQuery(eq("USER_OF"), anyList())).thenReturn(query);
        when(query.evaluate(eq(userId), anyList())).thenReturn(true);

        checker.check(userId, dto);

        verify(statsRepository, never()).increment(any());
    }
}