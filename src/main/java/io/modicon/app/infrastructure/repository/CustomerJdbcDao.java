package io.modicon.app.infrastructure.repository;

import io.modicon.app.domain.dao.CustomerDao;
import io.modicon.app.domain.model.Customer;
import io.modicon.app.domain.model.Payment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.util.NumberUtils;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Slf4j
@Repository("customerJdbc")
public class CustomerJdbcDao implements CustomerDao {

    private final JdbcTemplate jdbcTemplate;

    private final FindCustomerByEmail findCustomerByEmail;
    private final UpdateCustomer updateCustomer;
    private final InsertCustomer insertCustomer;

    public CustomerJdbcDao(JdbcTemplate jdbcTemplate,
                           DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.findCustomerByEmail = new FindCustomerByEmail(dataSource);
        this.updateCustomer = new UpdateCustomer(dataSource);
        this.insertCustomer = new InsertCustomer(dataSource);
    }

    RowMapper<Customer> rowMapper = (rs, rowNum) -> Customer.builder()
            .id(rs.getLong("id"))
            .name(rs.getString("name"))
            .email(rs.getString("email"))
            .age(rs.getInt("age"))
            .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
            .build();

    RowMapper<Payment> paymentRowMapper = (rs, rowNum) -> Payment.builder()
            .id(rs.getLong("id"))
            .customerId(rs.getLong("customer_id"))
            .amount(rs.getLong("amount"))
            .build();

    @Override
    public Optional<Customer> findByEmail(String email) {
        var sql = """
                SELECT * FROM customer
                WHERE email = ?
                """;
        return jdbcTemplate.query(sql, rowMapper, email).stream().findFirst();
    }

    public Optional<Customer> findByEmailMapped(String email) {
        Map<String, Object> params = new HashMap<>();
        params.put("email", email);
        return findCustomerByEmail.executeByNamedParam(params).stream().findFirst();
    }

    @Override
    public Customer findById(Long id) {
        var sql = """
                SELECT * FROM customer
                WHERE id = ?
                """;
        return jdbcTemplate.query(sql, rowMapper, id)
                .stream()
                .findFirst().orElseThrow(() -> new RuntimeException("customer not found"));
    }

    @Override
    public boolean existByEmail(String email) {
        var sql = """
                SELECT EXISTS (SELECT 1 FROM customer
                WHERE email = ?)
                """;
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, new Object[]{email}, Boolean.class));
    }

    @Override
    public boolean existById(Long id) {
        var sql = """
                SELECT EXISTS (SELECT 1 FROM customer
                WHERE id = ?)
                """;
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, new Object[]{id}, Boolean.class));
    }

    @Override
    public void addCustomer(Customer customer) {
        var sql = """
                INSERT INTO customer(name, email, age, created_at)
                VALUES(?, ?, ?, ?)
                """;
        jdbcTemplate.update(sql,
                customer.getName(),
                customer.getEmail(),
                customer.getAge(),
                LocalDateTime.now());
    }

    public Long addCustomerSqlUpdate(Customer customer) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", customer.getName());
        params.put("email", customer.getEmail());
        params.put("age", customer.getAge());
        params.put("created_at", LocalDateTime.now());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        insertCustomer.updateByNamedParam(params, keyHolder);
        return keyHolder.getKey().longValue();
    }

    public void addCustomerWithPaymentsBatchSqlUpdate(Customer customer) {
        BatchSqlInsertCustomerPayments batchSqlInsertCustomerPayments = new BatchSqlInsertCustomerPayments(jdbcTemplate.getDataSource());
        Map<String, Object> params = new HashMap<>();
        params.put("name", customer.getName());
        params.put("email", customer.getEmail());
        params.put("age", customer.getAge());
        params.put("created_at", LocalDateTime.now());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        insertCustomer.updateByNamedParam(params, keyHolder);

        customer = customer.withId(keyHolder.getKey().longValue());

        System.out.println("new customer: " + customer);

        Random random = new Random();
        int rowsAff = 0;
        for (int i = 0; i < 10; i++) {
                params = new HashMap<>();
                params.put("customer_id", customer.getId());
                params.put("amount", random.nextLong());
            rowsAff += batchSqlInsertCustomerPayments.updateByNamedParam(params);
        }
        log.info("BEFORE FLUSH");
        batchSqlInsertCustomerPayments.flush();

    }

    @Override
    public void updateCustomer(Customer oldCustomer, Customer newCustomer) {
        Customer customer = oldCustomer.toBuilder()
                .name(newCustomer.getName() != null ? newCustomer.getName() : oldCustomer.getName())
                .email(newCustomer.getEmail() != null ? newCustomer.getEmail() : oldCustomer.getEmail())
                .age(newCustomer.getAge() != null ? newCustomer.getAge() : oldCustomer.getAge())
                .build();

        var sql = "UPDATE customer SET name = ?, email = ?, age = ? WHERE id = ?";
        jdbcTemplate.update(sql,
                customer.getName(),
                customer.getEmail(),
                customer.getAge(),
                customer.getId());
    }

    public void updateCustomerSqlUpdate(Customer oldCustomer, Customer newCustomer) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", newCustomer.getName() != null ? newCustomer.getName() : oldCustomer.getName());
        params.put("email", newCustomer.getEmail() != null ? newCustomer.getEmail() : oldCustomer.getEmail());
        params.put("age", newCustomer.getAge() != null ? newCustomer.getAge() : oldCustomer.getAge());
        params.put("id", oldCustomer.getId());

        updateCustomer.updateByNamedParam(params);
    }

    @Override
    public void deleteCustomer(Long id) {
        var sql = "DELETE FROM customer WHERE id = ?";
        int deleted = jdbcTemplate.update(sql, id);
        if (deleted != 1) throw new RuntimeException("delete %d rows".formatted(deleted));
    }
}
