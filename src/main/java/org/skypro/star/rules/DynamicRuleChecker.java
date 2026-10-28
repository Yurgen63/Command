package org.skypro.star.rules;

import org.skypro.star.dto.RecommendationDto;
import org.skypro.star.dto.rule.QueryDto;
import org.skypro.star.dto.rule.RuleDto;
import org.skypro.star.repository.RuleStatsRepository;
import org.skypro.star.rules.query.RuleQuery;
import org.skypro.star.rules.query.RuleQueryFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class DynamicRuleChecker {

    private final RuleQueryFactory queryFactory;
    private final RuleStatsRepository ruleStatsRepository;

    public DynamicRuleChecker(RuleQueryFactory queryFactory, RuleStatsRepository ruleStatsRepository) {
        this.queryFactory = queryFactory;
        this.ruleStatsRepository = ruleStatsRepository;
    }

    public Optional<RecommendationDto> check(UUID userId, RuleDto rule) {
        for (QueryDto queryDto : rule.rule()) {
            RuleQuery query = queryFactory.getQuery(queryDto.query(), queryDto.arguments());

            boolean result = query.evaluate(userId, queryDto.arguments());

            if (queryDto.negate()) {
                result = !result;
            }

            if (!result) {
                return Optional.empty();
            }
        }

        if (rule.id() != null) {
            ruleStatsRepository.increment(rule.id());
        }

        return Optional.of(new RecommendationDto(
                rule.productName(),
                rule.productId(),
                rule.productText()
        ));
    }
}
