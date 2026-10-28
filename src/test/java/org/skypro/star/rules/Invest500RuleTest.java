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
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class Invest500RuleTest {

    @Mock
    RecommendationsRepository repository;
    private Invest500Rule rule;

    @BeforeEach
    void setUp() {
        rule = new Invest500Rule(repository);

        // По умолчанию: пользователь не имеет продуктов, суммы — нули.
        // lenient — потому что не все тесты дойдут до этих вызовов,
        // и strict stubs не должен ругаться.
        lenient().when(repository.hasProductType(org.mockito.ArgumentMatchers.any(UUID.class),
                        org.mockito.ArgumentMatchers.anyString()))
                .thenReturn(false);
        lenient().when(repository.getSumAmount(
                        org.mockito.ArgumentMatchers.any(UUID.class),
                        org.mockito.ArgumentMatchers.anyString(),
                        org.mockito.ArgumentMatchers.anyString()))
                .thenReturn(BigDecimal.ZERO);
    }

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
        // getSumAmount уже замокан в @BeforeEach на ZERO — NPE не будет

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