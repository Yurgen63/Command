package org.skypro.star.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "rule_stats")
public class RuleStatsEntity {

    @Id
    @Column(name = "rule_id")
    private Long ruleId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "rule_id")
    private RuleEntity rule;

    @Column(name = "count", nullable = false)
    private long count;

    public RuleStatsEntity() {}
    public RuleStatsEntity(RuleEntity rule) { this.rule = rule; this.count = 0; }

    public Long getRuleId() { return ruleId; }
    public RuleEntity getRule() { return rule; }
    public void setRule(RuleEntity rule) { this.rule = rule; }
    public long getCount() { return count; }
    public void setCount(long count) { this.count = count; }
}