package org.skypro.star.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.UUID;

@Repository
public class RecommendationsRepository {
    private final JdbcTemplate jdbcTemplate;

    public RecommendationsRepository(@Qualifier("recommendationsJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean hasProductType(UUID userId, String productType) {
        String sql = """
            SELECT COUNT(*) > 0 
            FROM transactions t
            JOIN products p ON t.product_id = p.id
            WHERE t.user_id = ? AND p.type = ?
        """;
        Boolean result = jdbcTemplate.queryForObject(sql, Boolean.class, userId, productType);
        return Boolean.TRUE.equals(result);
    }

    public BigDecimal getSumAmount(UUID userId, String productType, String transactionType) {
        String sql = """
            SELECT COALESCE(SUM(t.amount), 0)
            FROM transactions t
            JOIN products p ON t.product_id = p.id
            WHERE t.user_id = ? 
              AND p.type = ? 
              AND t.type = ?
        """;
        return jdbcTemplate.queryForObject(sql, BigDecimal.class, userId, productType, transactionType);
    }

    public TopSavingStats getTopSavingStats(UUID userId) {
        String sql = """
            SELECT 
                COALESCE(SUM(CASE WHEN p.type = 'DEBIT' AND t.type = 'DEPOSIT' THEN t.amount END), 0) as debit_dep,
                COALESCE(SUM(CASE WHEN p.type = 'SAVING' AND t.type = 'DEPOSIT' THEN t.amount END), 0) as saving_dep,
                COALESCE(SUM(CASE WHEN p.type = 'DEBIT' AND t.type = 'WITHDRAW' THEN t.amount END), 0) as debit_wit
            FROM transactions t
            JOIN products p ON t.product_id = p.id
            WHERE t.user_id = ?
        """;

        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> new TopSavingStats(
                rs.getBigDecimal("debit_dep"),
                rs.getBigDecimal("saving_dep"),
                rs.getBigDecimal("debit_wit")
        ), userId);
    }

    public record TopSavingStats(         // Вспомогательный класс-рекорд для хранения результата сложного запроса
            BigDecimal debitDeposits,
            BigDecimal savingDeposits,
            BigDecimal debitWithdrawals
    ) {}
}