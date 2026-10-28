package org.skypro.star.rules.query;

import java.util.List;
import java.util.UUID;

public interface RuleQuery {

    String getQueryType();
    boolean evaluate(UUID userId, List<String> arguments);
}