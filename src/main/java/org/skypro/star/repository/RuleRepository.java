package org.skypro.star.repository;

import org.skypro.star.entity.RuleEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RuleRepository extends JpaRepository<RuleEntity, Long> {

    @EntityGraph(attributePaths = "stats")
    @Query("select r from RuleEntity r")
    List<RuleEntity> findAllWithStats();

    @EntityGraph(attributePaths = {"queries", "queries.arguments"})
    @Query("select distinct r from RuleEntity r")
    List<RuleEntity> findAllWithQueries();
}