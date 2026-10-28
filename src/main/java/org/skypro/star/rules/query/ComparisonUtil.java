package org.skypro.star.rules.query;

import java.math.BigDecimal;

public final class ComparisonUtil {
    private ComparisonUtil() {}

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