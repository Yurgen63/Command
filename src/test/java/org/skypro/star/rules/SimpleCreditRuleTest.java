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
class SimpleCreditRuleTest {

    @Mock
    RecommendationsRepository repository;

    private SimpleCreditRule rule;

    @BeforeEach
    void setUp() {
        rule = new SimpleCreditRule(repository);

        lenient().when(repository.hasProductType(any(UUID.class), anyString()))
                .thenReturn(false);
        lenient().when(repository.getTopSavingStats(any(UUID.class)))
                .thenReturn(new RecommendationsRepository.TopSavingStats(
                        BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO));
    }

    @Test
    void recommendsWhenAllConditionsMet() {
        UUID userId = UUID.randomUUID();
        when(repository.hasProductType(userId, Constants.PRODUCT_TYPE_CREDIT)).thenReturn(false);
        when(repository.getTopSavingStats(userId))
                .thenReturn(new RecommendationsRepository.TopSavingStats(
                        new BigDecimal("200000"),  // debit deposits
                        BigDecimal.ZERO,
                        new BigDecimal("150000")   // withdrawals > 100000 и deposits > withdrawals
                ));

        Optional<RecommendationDto> result = rule.check(userId);

        assertTrue(result.isPresent());
        assertEquals("Простой кредит", result.get().name());
    }

    @Test
    void doesNotRecommendWhenHasCredit() {
        UUID userId = UUID.randomUUID();
        when(repository.hasProductType(userId, Constants.PRODUCT_TYPE_CREDIT)).thenReturn(true);

        assertTrue(rule.check(userId).isEmpty());
    }

    @Test
    void doesNotRecommendWhenWithdrawalsNotEnough() {
        UUID userId = UUID.randomUUID();
        when(repository.hasProductType(userId, Constants.PRODUCT_TYPE_CREDIT)).thenReturn(false);
        when(repository.getTopSavingStats(userId))
                .thenReturn(new RecommendationsRepository.TopSavingStats(
                        new BigDecimal("100000"),
                        BigDecimal.ZERO,
                        new BigDecimal("100000")   // ровно 100000 — не > 100000
                ));

        assertTrue(rule.check(userId).isEmpty());
    }

    @Test
    void doesNotRecommendWhenWithdrawalsExceedDeposits() {
        UUID userId = UUID.randomUUID();
        when(repository.hasProductType(userId, Constants.PRODUCT_TYPE_CREDIT)).thenReturn(false);
        when(repository.getTopSavingStats(userId))
                .thenReturn(new RecommendationsRepository.TopSavingStats(
                        new BigDecimal("100000"),
                        BigDecimal.ZERO,
                        new BigDecimal("150000")   // withdrawals > deposits
                ));

        assertTrue(rule.check(userId).isEmpty());
    }
}
