package org.skypro.star.rules.query.impl;

import org.skypro.star.repository.RecommendationsRepository;
import org.skypro.star.rules.query.ComparisonUtil;
import org.skypro.star.rules.query.RuleQuery;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class DepositWithdrawCompareQuery implements RuleQuery {
    private final RecommendationsRepository repository;

    public DepositWithdrawCompareQuery(RecommendationsRepository repository) {
        this.repository = repository;
    }

    @Override
    public String getQueryType() {
        return "TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW";
    }

    @Override
    public boolean evaluate(UUID userId, List<String> arguments) {
        // Аргументы: [productType, operator]
        String productType = arguments.get(0);
        String operator = arguments.get(1);

        var stats = repository.getDepositWithdrawStats(userId, productType);

        return ComparisonUtil.compare(stats.deposits(), stats.withdrawals(), operator);
    }
}