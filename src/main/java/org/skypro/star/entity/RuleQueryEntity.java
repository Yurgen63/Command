package org.skypro.star.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rule_query")
public class RuleQueryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rule_id", nullable = false)
    private RuleEntity rule;

    @Column(name = "query", nullable = false, length = 64)
    private String query;

    @Column(name = "negate", nullable = false)
    private boolean negate;

    @Column(name = "position", nullable = false)
    private int position;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "rule_query_argument",
            joinColumns = @JoinColumn(name = "query_id")
    )
    @OrderColumn(name = "position")
    @Column(name = "argument", nullable = false, length = 64)
    private List<String> arguments = new ArrayList<>();

    public RuleQueryEntity() {}

    public RuleQueryEntity(String query, List<String> arguments, boolean negate) {
        this.query = query;
        this.arguments = new ArrayList<>(arguments);
        this.negate = negate;
    }

    public Long getId() { return id; }
    public RuleEntity getRule() { return rule; }
    public void setRule(RuleEntity rule) { this.rule = rule; }
    public String getQuery() { return query; }
    public void setQuery(String query) { this.query = query; }
    public boolean isNegate() { return negate; }
    public void setNegate(boolean negate) { this.negate = negate; }
    public int getPosition() { return position; }
    public void setPosition(int position) { this.position = position; }
    public List<String> getArguments() { return arguments; }
    public void setArguments(List<String> arguments) { this.arguments = arguments; }
}