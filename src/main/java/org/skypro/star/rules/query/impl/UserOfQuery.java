package org.skypro.star.rules.query.impl;

import org.skypro.star.repository.RecommendationsRepository;
import org.skypro.star.rules.query.RuleQuery;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.UUID;

@Component
public class UserOfQuery implements RuleQuery {
    private final RecommendationsRepository repository;

    public UserOfQuery(RecommendationsRepository repository) {
        this.repository = repository;
    }

    @Override
    public String getQueryType() {
        return "USER_OF";
    }

    @Override
    public boolean evaluate(UUID userId, List<String> arguments) {
        String productType = arguments.get(0);
        return repository.hasProductType(userId, productType);
    }
}