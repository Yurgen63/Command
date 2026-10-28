package org.skypro.star.service;

import org.skypro.star.dto.rule.RuleDto;
import org.skypro.star.dto.rule.RuleStatsDto;
import org.skypro.star.entity.RuleEntity;
import org.skypro.star.entity.RuleStatsEntity;
import org.skypro.star.exception.RuleNotFoundException;
import org.skypro.star.mapper.RuleMapper;
import org.skypro.star.repository.RuleRepository;
import org.skypro.star.repository.RuleStatsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RuleService {

    private final RuleRepository repository;
    private final RuleMapper mapper;

    public RuleService(RuleRepository repository, RuleMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional
    public RuleDto create(RuleDto dto) {
        RuleEntity entity = mapper.toEntity(dto);
        RuleEntity saved = repository.save(entity);
        return mapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    public List<RuleDto> findAll() {
        return repository.findAllWithQueries().stream()
                .map(mapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RuleStatsDto> getStats() {
        return repository.findAllWithStats().stream()
                .map(rule -> new RuleStatsDto(
                        String.valueOf(rule.getId()),
                        String.valueOf(rule.getStats() != null ? rule.getStats().getCount() : 0L)
                ))
                .toList();
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new RuleNotFoundException(id);
        }
        repository.deleteById(id);
    }
}
