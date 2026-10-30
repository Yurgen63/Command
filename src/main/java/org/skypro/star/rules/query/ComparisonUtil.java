package org.skypro.star.rules.query;

import java.math.BigDecimal;
/**
 * Утилита для сравнения {@link BigDecimal} с оператором, заданным строкой.
 * Используется в запросах динамических правил.
 */
public final class ComparisonUtil {
    private ComparisonUtil() {}
    /**
     * Сравнивает два числа с помощью указанного оператора.
     *
     * @param left     левый операнд
     * @param right    правый операнд
     * @param operator один из {@code >}, {@code <}, {@code =}, {@code >=}, {@code <=}
     * @return результат сравнения
     * @throws IllegalArgumentException если оператор неизвестен
     */
    public static boolean compare(BigDecimal left, BigDecimal right, String operator) {
        int cmp = left.compareTo(right);
        return switch (operator) {
            case ">"  -> cmp > 0;
            case "<"  -> cmp < 0;
            case "="  -> cmp == 0;
            case ">=" -> cmp >= 0;
            case "<=" -> cmp <= 0;
            default -> throw new IllegalArgumentException("Неизвестный оператор сравнения: " + operator);
        };
    }
}