package org.skypro.star.mapper;

import org.skypro.star.dto.rule.QueryDto;
import org.skypro.star.dto.rule.RuleDto;
import org.skypro.star.entity.RuleEntity;
import org.skypro.star.entity.RuleQueryEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RuleMapper {

    public RuleEntity toEntity(RuleDto dto) {
        RuleEntity entity = new RuleEntity(
                dto.productName(),
                dto.productId(),
                dto.productText()
        );
        for (QueryDto q : dto.rule()) {
            entity.addQuery(new RuleQueryEntity(
                    q.query(),
                    q.arguments(),
                    q.negate()
            ));
        }
        return entity;
    }

    public RuleDto toDto(RuleEntity entity) {
        List<QueryDto> queries = entity.getQueries().stream()
                .map(q -> new QueryDto(q.getQuery(), q.getArguments(), q.isNegate()))
                .toList();
        return new RuleDto(
                entity.getId(),
                entity.getProductName(),
                entity.getProductId(),
                entity.getProductText(),
                queries
        );
    }
}