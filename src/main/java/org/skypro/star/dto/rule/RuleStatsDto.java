package org.skypro.star.dto.rule;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RuleStatsDto(
        @JsonProperty("rule_id")
        String ruleId,

        @JsonProperty("count")
        String count
) {}