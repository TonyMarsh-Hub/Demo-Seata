package io.seata.sample.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

    private final JdbcTemplate jdbcTemplate;

    public AccountService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void reduce(String userId, int money) {
        jdbcTemplate.update("update account_tbl set money = money - ? where user_id = ?", new Object[]{money, userId});
    }
}