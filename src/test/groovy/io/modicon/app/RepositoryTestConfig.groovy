package io.modicon.app

import io.modicon.app.domain.model.Customer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.simple.SimpleJdbcInsert
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.jdbc.support.KeyHolder
import spock.lang.Specification

import javax.sql.DataSource
import java.sql.PreparedStatement
import java.sql.Statement
import java.sql.Timestamp
import java.time.LocalDateTime

@DataJdbcTest
class RepositoryTestConfig extends Specification {
    @Autowired
    protected JdbcTemplate jdbcTemplate
    @Autowired
    protected DataSource dataSource

    protected final String CUSTOMER_NAME = "name"
    protected final String CUSTOMER_EMAIL = "email"
    protected final int CUSTOMER_AGE = 30
    protected final LocalDateTime CUSTOMER_CREATED_AT = LocalDateTime.now()

    protected final Customer CUSTOMER = Customer.builder()
            .name(CUSTOMER_NAME)
            .email(CUSTOMER_EMAIL)
            .age(CUSTOMER_AGE)
            .build()
    protected final Customer NEW_CUSTOMER = Customer.builder()
            .name(CUSTOMER_NAME)
            .email('new' + CUSTOMER_EMAIL)
            .age(CUSTOMER_AGE)
            .build()

    protected Long CUSTOMER_ID

    protected final String CUSTOMER_NOT_EXIST_EMAIL = "not exist email"

    void setup() {

        var sql = "INSERT INTO customer (name, email, age, created_at) VALUES (?, ?, ?, ?)"
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            def ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
            ps.setString(1, CUSTOMER.getName())
            ps.setString(2, CUSTOMER.getEmail())
            ps.setInt(3, CUSTOMER.getAge())
            ps.setTimestamp(4, Timestamp.valueOf(CUSTOMER_CREATED_AT))
            return ps
        }, keyHolder)
        CUSTOMER_ID = keyHolder.getKey().longValue()
//        jdbcTemplate.update(sql, customer.getName(), customer.getEmail(), customer.getAge(), customer.getCreatedAt())
    }
}