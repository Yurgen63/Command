package org.skypro.star.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.skypro.star.entity.RuleStatsEntity;

public interface RuleStatsRepository extends JpaRepository<RuleStatsEntity, Long> {

    @Modifying
    @Transactional
    @Query("update RuleStatsEntity s set s.count = s.count + 1 where s.ruleId = :ruleId")
    int increment(@Param("ruleId") Long ruleId);
}