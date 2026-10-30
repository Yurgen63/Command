package org.skypro.star.rules.query;

import java.util.List;
import java.util.UUID;

/**
 * Один запрос внутри динамического правила рекомендаций.
 * <p>
 * Реализации регистрируются как Spring-бины и собираются в
 * {@link RuleQueryFactory} по типу, возвращаемому {@link #getQueryType()}.
 */
public interface RuleQuery {

    String getQueryType();

    /**
     * Вычисляет результат запроса для пользователя.
     *
     * @param userId    идентификатор пользователя
     * @param arguments аргументы запроса (уже провалидированы фабрикой)
     * @return {@code true}, если условие выполнено
     */
    boolean evaluate(UUID userId, List<String> arguments);
}