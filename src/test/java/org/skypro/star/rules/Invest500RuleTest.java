package org.skypro.star.rules;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.skypro.star.repository.RecommendationsRepository;
import org.skypro.star.utils.Constants;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class Invest500RuleTest {

    @Mock
    RecommendationsRepository repository;
    private Invest500Rule rule;

    @BeforeEach
    void setUp() { rule = new Invest500Rule(repository); }

    @Test
    void recommendsWhenAllConditionsMet() {
        UUID userId = UUID.randomUUID();
        when(repository.hasProductType(userId, Constants.PRODUCT_TYPE_DEBIT)).thenReturn(true);
        when(repository.hasProductType(userId, Constants.PRODUCT_TYPE_INVEST)).thenReturn(false);
        when(repository.getSumAmount(userId, Constants.PRODUCT_TYPE_SAVING,
                Constants.TRANSACTION_TYPE_DEPOSIT)).thenReturn(new BigDecimal("1500"));

        assertTrue(rule.check(userId).isPresent());
    }

    @Test
    void doesNotRecommendWhenHasInvest() {
        UUID userId = UUID.randomUUID();
        when(repository.hasProductType(userId, Constants.PRODUCT_TYPE_DEBIT)).thenReturn(true);
        when(repository.hasProductType(userId, Constants.PRODUCT_TYPE_INVEST)).thenReturn(true);
        // дальше условие уже не проверяется — sumSaving не мокаем (strict stubs)

        assertTrue(rule.check(userId).isEmpty());
    }

    @Test
    void doesNotRecommendWhenSavingNotEnough() {
        UUID userId = UUID.randomUUID();
        when(repository.hasProductType(userId, Constants.PRODUCT_TYPE_DEBIT)).thenReturn(true);
        when(repository.hasProductType(userId, Constants.PRODUCT_TYPE_INVEST)).thenReturn(false);
        when(repository.getSumAmount(userId, Constants.PRODUCT_TYPE_SAVING,
                Constants.TRANSACTION_TYPE_DEPOSIT)).thenReturn(new BigDecimal("1000"));

        assertTrue(rule.check(userId).isEmpty());  // 1000 — не > 1000
    }
}
