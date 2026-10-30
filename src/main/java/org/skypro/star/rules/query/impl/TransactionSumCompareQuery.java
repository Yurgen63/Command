package org.skypro.star.rules.query.impl;

import org.skypro.star.repository.RecommendationsRepository;
import org.skypro.star.rules.query.ComparisonUtil;
import org.skypro.star.rules.query.RuleQuery;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
/**
 * Запрос {@code TRANSACTION_SUM_COMPARE}: сравнивает сумму транзакций
 * заданного типа по продукту с константой.
 * Аргументы: {@code [productType, transactionType, operator, constant]}.
 */
@Component
public class TransactionSumCompareQuery implements RuleQuery {
    private final RecommendationsRepository repository;

    public TransactionSumCompareQuery(RecommendationsRepository repository) {
        this.repository = repository;
    }

    @Override
    public String getQueryType() {
        return "TRANSACTION_SUM_COMPARE";
    }

    @Override
    public boolean evaluate(UUID userId, List<String> arguments) {
        // Аргументы: [productType, transactionType, operator, constant]
        String productType = arguments.get(0);
        String transactionType = arguments.get(1);
        String operator = arguments.get(2);
        BigDecimal constant = new BigDecimal(arguments.get(3));

        BigDecimal sum = repository.getSumAmount(userId, productType, transactionType);

        return ComparisonUtil.compare(sum, constant, operator);
    }
}