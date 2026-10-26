package org.skypro.star.repository;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.UUID;

@Repository
public class RecommendationsRepository {
    private static final Logger log = LoggerFactory.getLogger(RecommendationsRepository.class);
    private final JdbcTemplate jdbcTemplate;

    private final Cache<CountKey, Integer> countCache;
    private final Cache<SumKey, BigDecimal> sumCache;
    private final Cache<StatsKey, DepositWithdrawStats> statsCache;

    public RecommendationsRepository(@Qualifier("recommendationsJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;

        this.countCache = Caffeine.newBuilder()
                .maximumSize(10_000)
                .expireAfterWrite(Duration.ofMinutes(30))
                .build();

        this.sumCache = Caffeine.newBuilder()
                .maximumSize(10_000)
                .expireAfterWrite(Duration.ofMinutes(30))
                .build();

        this.statsCache = Caffeine.newBuilder()
                .maximumSize(10_000)
                .expireAfterWrite(Duration.ofMinutes(30))
                .build();
    }

    public int countTransactions(UUID userId, String productType) {
        var key = new CountKey(userId, productType);
        return countCache.get(key, k -> {
            log.info(">>> CACHE MISS [countCache] key={}", k);
            String sql = """
                SELECT COUNT(*)
                FROM transactions t
                JOIN products p ON t.product_id = p.id
                WHERE t.user_id = ? AND p.type = ?
            """;
            Integer result = jdbcTemplate.queryForObject(sql, Integer.class, k.userId(), k.productType());
            return result != null ? result : 0;
        });
    }

    public boolean hasProductType(UUID userId, String productType) {
        return countTransactions(userId, productType) > 0;
    }

    public BigDecimal getSumAmount(UUID userId, String productType, String transactionType) {
        var key = new SumKey(userId, productType, transactionType);
        return sumCache.get(key, k -> {
            log.info(">>> CACHE MISS [sumCache] key={}", k);
            String sql = """
                SELECT COALESCE(SUM(t.amount), 0)
                FROM transactions t
                JOIN products p ON t.product_id = p.id
                WHERE t.user_id = ? 
                  AND p.type = ? 
                  AND t.type = ?
            """;
            return jdbcTemplate.queryForObject(sql, BigDecimal.class,
                    k.userId(), k.productType(), k.transactionType());
        });
    }

    public DepositWithdrawStats getDepositWithdrawStats(UUID userId, String productType) {
        var key = new StatsKey(userId, productType);
        return statsCache.get(key, k -> {
            log.info(">>> CACHE MISS [statsCache] key={}", k);
            String sql = """
                SELECT 
                    COALESCE(SUM(CASE WHEN t.type = 'DEPOSIT' THEN t.amount END), 0) as deposits,
                    COALESCE(SUM(CASE WHEN t.type = 'WITHDRAW' THEN t.amount END), 0) as withdrawals
                FROM transactions t
                JOIN products p ON t.product_id = p.id
                WHERE t.user_id = ? AND p.type = ?
            """;
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> new DepositWithdrawStats(
                    rs.getBigDecimal("deposits"),
                    rs.getBigDecimal("withdrawals")
            ), k.userId(), k.productType());
        });
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

    private record CountKey(UUID userId, String productType) {}
    private record SumKey(UUID userId, String productType, String transactionType) {}
    private record StatsKey(UUID userId, String productType) {}

    public record DepositWithdrawStats(BigDecimal deposits, BigDecimal withdrawals) {}
    public record TopSavingStats(BigDecimal debitDeposits, BigDecimal savingDeposits, BigDecimal debitWithdrawals) {}
}