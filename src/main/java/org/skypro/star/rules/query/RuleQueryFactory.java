package org.skypro.star.rules.query;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
/**
 * Фабрика запросов динамических правил.
 * <p>
 * Собирает все бины {@link RuleQuery} в map по типу и валидирует аргументы
 * перед выдачей запроса. Валидация проверяет:
 * <ul>
 *   <li>известность типа запроса;</li>
 *   <li>корректное количество аргументов;</li>
 *   <li>допустимые значения типов продукта, типа транзакции и оператора;</li>
 *   <li>что константа сравнения — число.</li>
 * </ul>
 */
@Component
public class RuleQueryFactory {

    private final Map<String, RuleQuery> queries;

    private static final Set<String> VALID_PRODUCTS = Set.of("DEBIT", "CREDIT", "INVEST", "SAVING");
    private static final Set<String> VALID_TRANSACTIONS = Set.of("DEPOSIT", "WITHDRAW");
    private static final Set<String> VALID_OPERATORS = Set.of(">", "<", "=", ">=", "<=");

    public RuleQueryFactory(List<RuleQuery> queryList) {
        this.queries = queryList.stream()
                .collect(Collectors.toMap(RuleQuery::getQueryType, Function.identity()));
    }
    /**
     * Возвращает реализацию запроса по его типу и валидирует аргументы.
     *
     * @param type      тип запроса
     * @param arguments аргументы запроса
     * @return реализация {@link RuleQuery}
     * @throws IllegalArgumentException если тип неизвестен или аргументы
     *                                  не прошли валидацию
     */
    public RuleQuery getQuery(String type, List<String> arguments) {
        RuleQuery query = queries.get(type);
        if (query == null) {
            throw new IllegalArgumentException("Неизвестный тип запроса: " + type);
        }

        validateArguments(type, arguments);
        return query;
    }

    private void validateArguments(String type, List<String> args) {
        switch (type) {
            case "USER_OF", "ACTIVE_USER_OF" -> {
                if (args.size() != 1) throw new IllegalArgumentException(type + " требует 1 аргумент");
                if (!VALID_PRODUCTS.contains(args.get(0))) throw new IllegalArgumentException("Неверный тип продукта: " + args.get(0));
            }
            case "TRANSACTION_SUM_COMPARE" -> {
                if (args.size() != 4) throw new IllegalArgumentException(type + " требует 4 аргумента");
                if (!VALID_PRODUCTS.contains(args.get(0))) throw new IllegalArgumentException("Неверный тип продукта: " + args.get(0));
                if (!VALID_TRANSACTIONS.contains(args.get(1))) throw new IllegalArgumentException("Неверный тип транзакции: " + args.get(1));
                if (!VALID_OPERATORS.contains(args.get(2))) throw new IllegalArgumentException("Неверный оператор: " + args.get(2));
                try {
                    new java.math.BigDecimal(args.get(3));
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Четвертый аргумент должен быть числом: " + args.get(3));
                }
            }
            case "TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW" -> {
                if (args.size() != 2) throw new IllegalArgumentException(type + " требует 2 аргумента");
                if (!VALID_PRODUCTS.contains(args.get(0))) throw new IllegalArgumentException("Неверный тип продукта: " + args.get(0));
                if (!VALID_OPERATORS.contains(args.get(1))) throw new IllegalArgumentException("Неверный оператор: " + args.get(1));
            }
            default -> throw new IllegalArgumentException("Неизвестный тип запроса: " + type);
        }
    }
}