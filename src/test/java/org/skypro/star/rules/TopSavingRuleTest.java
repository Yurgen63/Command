package org.skypro.star.rules;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.skypro.star.dto.RecommendationDto;
import org.skypro.star.repository.RecommendationsRepository;
import org.skypro.star.utils.Constants;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TopSavingRuleTest {

    @Mock
    RecommendationsRepository repository;

    private TopSavingRule rule;

    @BeforeEach
    void setUp() {
        rule = new TopSavingRule(repository);

        lenient().when(repository.hasProductType(any(UUID.class), anyString()))
                .thenReturn(false);
        lenient().when(repository.getTopSavingStats(any(UUID.class)))
                .thenReturn(new RecommendationsRepository.TopSavingStats(
                        BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO));
    }

    @Test
    void recommendsWhenAllConditionsMetByDebit() {
        UUID userId = UUID.randomUUID();
        when(repository.hasProductType(userId, Constants.PRODUCT_TYPE_DEBIT)).thenReturn(true);
        when(repository.getTopSavingStats(userId))
                .thenReturn(new RecommendationsRepository.TopSavingStats(
                        new BigDecimal("60000"),   // debit deposits ≥ 50000
                        BigDecimal.ZERO,
                        new BigDecimal("30000")    // debit deposits > withdrawals
                ));

        Optional<RecommendationDto> result = rule.check(userId);

        assertTrue(result.isPresent());
        assertEquals("Top Saving", result.get().name());
    }

    @Test
    void recommendsWhenSavingDepositsEnough() {
        UUID userId = UUID.randomUUID();
        when(repository.hasProductType(userId, Constants.PRODUCT_TYPE_DEBIT)).thenReturn(true);
        when(repository.getTopSavingStats(userId))
                .thenReturn(new RecommendationsRepository.TopSavingStats(
                        new BigDecimal("10000"),   // debit deposits < 50000
                        new BigDecimal("50000"),   // saving deposits ≥ 50000
                        new BigDecimal("5000")     // debit deposits > withdrawals
                ));

        assertTrue(rule.check(userId).isPresent());
    }

    @Test
    void doesNotRecommendWhenNoDebit() {
        UUID userId = UUID.randomUUID();
        when(repository.hasProductType(userId, Constants.PRODUCT_TYPE_DEBIT)).thenReturn(false);

        assertTrue(rule.check(userId).isEmpty());
    }

    @Test
    void doesNotRecommendWhenDepositsNotEnough() {
        UUID userId = UUID.randomUUID();
        when(repository.hasProductType(userId, Constants.PRODUCT_TYPE_DEBIT)).thenReturn(true);
        when(repository.getTopSavingStats(userId))
                .thenReturn(new RecommendationsRepository.TopSavingStats(
                        new BigDecimal("49999"),
                        new BigDecimal("49999"),
                        new BigDecimal("10000")
                ));

        assertTrue(rule.check(userId).isEmpty());
    }

    @Test
    void doesNotRecommendWhenWithdrawalsExceedDeposits() {
        UUID userId = UUID.randomUUID();
        when(repository.hasProductType(userId, Constants.PRODUCT_TYPE_DEBIT)).thenReturn(true);
        when(repository.getTopSavingStats(userId))
                .thenReturn(new RecommendationsRepository.TopSavingStats(
                        new BigDecimal("60000"),
                        BigDecimal.ZERO,
                        new BigDecimal("70000")    // withdrawals > deposits
                ));

        assertTrue(rule.check(userId).isEmpty());
    }
}