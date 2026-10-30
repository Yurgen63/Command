package org.skypro.star.rules.query.impl;

import org.skypro.star.repository.RecommendationsRepository;
import org.skypro.star.rules.query.RuleQuery;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.UUID;
/**
 * Запрос {@code ACTIVE_USER_OF}: проверяет, что у пользователя не менее
 * пяти транзакций по продукту указанного типа.
 * Аргументы: {@code [productType]}.
 */
@Component
public class ActiveUserOfQuery implements RuleQuery {
    private final RecommendationsRepository repository;

    public ActiveUserOfQuery(RecommendationsRepository repository) {
        this.repository = repository;
    }

    @Override
    public String getQueryType() {
        return "ACTIVE_USER_OF";
    }

    @Override
    public boolean evaluate(UUID userId, List<String> arguments) {
        String productType = arguments.get(0);
        // В ТЗ сказано: "есть хотя бы пять транзакций"
        return repository.countTransactions(userId, productType) >= 5;
    }
}