package org.skypro.star.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "rule")
public class RuleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "product_text", nullable = false)
    private String productText;

    @OneToMany(mappedBy = "rule", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("position ASC")
    private List<RuleQueryEntity> queries = new ArrayList<>();

    public RuleEntity() {}

    public RuleEntity(String productName, UUID productId, String productText) {
        this.productName = productName;
        this.productId = productId;
        this.productText = productText;
    }

    public void addQuery(RuleQueryEntity query) {
        query.setRule(this);
        query.setPosition(queries.size());
        queries.add(query);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public UUID getProductId() { return productId; }
    public void setProductId(UUID productId) { this.productId = productId; }
    public String getProductText() { return productText; }
    public void setProductText(String productText) { this.productText = productText; }
    public List<RuleQueryEntity> getQueries() { return queries; }
    public void setQueries(List<RuleQueryEntity> queries) { this.queries = queries; }
}