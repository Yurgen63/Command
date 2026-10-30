package org.skypro.star.rules;

import org.skypro.star.dto.RecommendationDto;

import java.util.Optional;
import java.util.UUID;

/**
 * Статическое правило рекомендаций.
 * <p>
 * Реализации регистрируются как Spring-бины и автоматически подхватываются
 * {@link org.skypro.star.service.RecommendationService}. Новое правило
 * добавляется созданием нового {@code @Component}-класса без изменения
 * существующего кода (принцип Open/Closed).
 */
public interface RecommendationRuleSet {
    /**
     * Проверяет, подходит ли продукт пользователю по этому правилу.
     *
     * @param userId идентификатор пользователя
     * @return рекомендация, если правило сработало; иначе {@link Optional#empty()}
     */
    Optional<RecommendationDto> check(UUID userId);
}
