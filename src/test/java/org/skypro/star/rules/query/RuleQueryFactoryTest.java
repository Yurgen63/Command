package org.skypro.star.rules.query;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RuleQueryFactoryTest {

    @Mock RuleQuery userOf;
    @Mock RuleQuery activeUserOf;
    @Mock RuleQuery txSumCompare;
    @Mock RuleQuery txSumCompareDepositWithdraw;

    private RuleQueryFactory factory;

    @BeforeEach
    void setUp() {
        when(userOf.getQueryType()).thenReturn("USER_OF");
        when(activeUserOf.getQueryType()).thenReturn("ACTIVE_USER_OF");
        when(txSumCompare.getQueryType()).thenReturn("TRANSACTION_SUM_COMPARE");
        when(txSumCompareDepositWithdraw.getQueryType())
                .thenReturn("TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW");

        factory = new RuleQueryFactory(
                List.of(userOf, activeUserOf, txSumCompare, txSumCompareDepositWithdraw));
    }

    // ---------- USER_OF / ACTIVE_USER_OF ----------

    @Test
    void throwsOnUnknownQueryType() {
        assertThrows(IllegalArgumentException.class,
                () -> factory.getQuery("UNKNOWN", List.of()));
    }

    @Test
    void throwsOnWrongArgCountForUserOf() {
        assertThrows(IllegalArgumentException.class,
                () -> factory.getQuery("USER_OF", List.of("DEBIT", "CREDIT")));
    }

    @Test
    void throwsOnInvalidProductForUserOf() {
        assertThrows(IllegalArgumentException.class,
                () -> factory.getQuery("USER_OF", List.of("GOLD")));
    }

    @Test
    void acceptsValidUserOf() {
        assertDoesNotThrow(() -> factory.getQuery("USER_OF", List.of("DEBIT")));
    }

    // ---------- TRANSACTION_SUM_COMPARE ----------

    @Test
    void throwsOnWrongArgCountForTxSumCompare() {
        assertThrows(IllegalArgumentException.class,
                () -> factory.getQuery("TRANSACTION_SUM_COMPARE",
                        List.of("DEBIT", "DEPOSIT", ">")));
    }

    @Test
    void throwsOnInvalidTransactionType() {
        assertThrows(IllegalArgumentException.class,
                () -> factory.getQuery("TRANSACTION_SUM_COMPARE",
                        List.of("DEBIT", "TRANSFER", ">", "1000")));
    }

    @Test
    void throwsOnInvalidOperatorForTxSumCompare() {
        assertThrows(IllegalArgumentException.class,
                () -> factory.getQuery("TRANSACTION_SUM_COMPARE",
                        List.of("DEBIT", "DEPOSIT", "==", "1000")));
    }

    @Test
    void throwsWhenConstantNotANumber() {
        assertThrows(IllegalArgumentException.class,
                () -> factory.getQuery("TRANSACTION_SUM_COMPARE",
                        List.of("DEBIT", "DEPOSIT", ">", "abc")));
    }

    @Test
    void acceptsValidTxSumCompare() {
        assertDoesNotThrow(() -> factory.getQuery("TRANSACTION_SUM_COMPARE",
                List.of("DEBIT", "DEPOSIT", ">", "1000")));
    }

    // ---------- TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW ----------

    @Test
    void throwsOnWrongArgCountForDepositWithdrawCompare() {
        assertThrows(IllegalArgumentException.class,
                () -> factory.getQuery("TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW",
                        List.of("DEBIT")));
    }

    @Test
    void throwsOnInvalidOperatorForDepositWithdrawCompare() {
        assertThrows(IllegalArgumentException.class,
                () -> factory.getQuery("TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW",
                        List.of("DEBIT", "==")));
    }

    @Test
    void acceptsValidDepositWithdrawCompare() {
        assertDoesNotThrow(() -> factory.getQuery(
                "TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW",
                List.of("DEBIT", ">")));
    }
}