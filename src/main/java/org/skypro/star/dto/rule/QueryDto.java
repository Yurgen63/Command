package org.skypro.star.dto.rule;

import java.util.List;

public record QueryDto(
        String query,
        List<String> arguments,
        boolean negate
) {
}