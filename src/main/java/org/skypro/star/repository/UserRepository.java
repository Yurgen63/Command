package org.skypro.star.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserRepository(@Qualifier("recommendationsJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public record UserRow(UUID id, String username, String firstName, String lastName) {}

    public List<UserRow> findByUsername(String username) {
        String sql = """
            SELECT id, username, first_name, last_name
            FROM users
            WHERE LOWER(username) = LOWER(?)
        """;
        return jdbcTemplate.query(sql, (rs, i) -> new UserRow(
                rs.getObject("id", UUID.class),
                rs.getString("username"),
                rs.getString("first_name"),
                rs.getString("last_name")
        ), username);
    }
}