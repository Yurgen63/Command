package org.skypro.star.service;

import org.skypro.star.dto.RecommendationDto;
import org.skypro.star.mapper.RuleMapper;
import org.skypro.star.repository.RuleRepository;
import org.skypro.star.rules.DynamicRuleChecker;
import org.skypro.star.rules.RecommendationRuleSet;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
/**
 * Сервис формирования рекомендаций банковских продуктов.
 * <p>
 * Объединяет результаты двух источников:
 * <ul>
 *   <li>статических правил ({@link RecommendationRuleSet}), зарегистрированных как Spring-бины;</li>
 *   <li>динамических правил, хранящихся в БД и проверяемых через {@link DynamicRuleChecker}.</li>
 * </ul>
 * Порядок в результирующем списке: сначала статические рекомендации, затем динамические.
 */
@Service
public class RecommendationService {

    private final List<RecommendationRuleSet> fixedRules;
    private final RuleRepository ruleRepository;
    private final RuleMapper ruleMapper;
    private final DynamicRuleChecker dynamicRuleChecker;

    public RecommendationService(
            List<RecommendationRuleSet> fixedRules,
            RuleRepository ruleRepository,
            RuleMapper ruleMapper,
            DynamicRuleChecker dynamicRuleChecker
    ) {
        this.fixedRules = fixedRules;
        this.ruleRepository = ruleRepository;
        this.ruleMapper = ruleMapper;
        this.dynamicRuleChecker = dynamicRuleChecker;
    }

    public List<RecommendationDto> getRecommendations(UUID userId) {
        List<RecommendationDto> result = new ArrayList<>();

        fixedRules.stream()
                .map(rule -> rule.check(userId))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(result::add);

        ruleRepository.findAllWithQueries().stream()
                .map(ruleMapper::toDto)
                .map(dto -> dynamicRuleChecker.check(userId, dto))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(result::add);

        return result;
    }
}