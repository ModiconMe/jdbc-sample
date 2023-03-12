package io.modicon.app.infrastructure.repository

import io.modicon.app.RepositoryTestConfig
import org.springframework.dao.DuplicateKeyException

class CustomerJdbcDaoTest extends RepositoryTestConfig {

    private CustomerJdbcDao customerJdbcDao

    void setup() {
        this.customerJdbcDao = new CustomerJdbcDao(jdbcTemplate, dataSource)
    }

    def "should find customer by email"() {
        when: 'find customer by email'
        var actual = customerJdbcDao.findByEmail(CUSTOMER_EMAIL)

        then: 'return customer'
        actual.isPresent()
        verifyAll(actual.get()) {
            getName() == CUSTOMER_NAME
            getEmail() == CUSTOMER_EMAIL
            getAge() == CUSTOMER_AGE
            getCreatedAt() != null
        }
    }

    def "should find customer by email mapped"() {
        when: 'find customer by email'
        var actual = customerJdbcDao.findByEmailMapped(CUSTOMER_EMAIL)

        then: 'return customer'
        actual.isPresent()
        verifyAll(actual.get()) {
            getName() == CUSTOMER_NAME
            getEmail() == CUSTOMER_EMAIL
            getAge() == CUSTOMER_AGE
            getCreatedAt() != null
        }
    }


    def "should throw when customer is not found by email"() {
        when: 'find customer by email'
        def actual = customerJdbcDao.findByEmail(CUSTOMER_NOT_EXIST_EMAIL)

        then: 'throw exception'
        actual.isEmpty()
    }

    def "should find customer by id"() {
        when: 'find customer by id'
        var actual = customerJdbcDao.findById(CUSTOMER_ID)


        then: 'return customer'
        verifyAll(actual) {
            getName() == CUSTOMER_NAME
            getEmail() == CUSTOMER_EMAIL
            getAge() == CUSTOMER_AGE
            getCreatedAt() != null
        }
    }

    def "should throw when customer is not found by id"() {
        when: 'find customer by id'
        customerJdbcDao.findById(2)

        then: 'throw exception'
        def e = thrown(RuntimeException)
        e.getMessage() == 'customer not found'
    }

    def "should check customer exist by email"() {
        when: 'check customer by email'
        def actual = customerJdbcDao.existByEmail(CUSTOMER_EMAIL)

        then: 'return true'
        actual
    }

    def "should check customer not exist by email"() {
        when: 'check customer by email'
        def actual = customerJdbcDao.existByEmail(CUSTOMER_NOT_EXIST_EMAIL)

        then: 'return false'
        !actual
    }

    def "should check customer exist by id"() {
        when: 'check customer by id'
        def actual = customerJdbcDao.existById(CUSTOMER_ID)

        then: 'return true'
        actual
    }

    def "should check customer not exist by id"() {
        when: 'check customer by id'
        def actual = customerJdbcDao.existById(2)

        then: 'return false'
        !actual
    }

    def "should add customer successfully"() {
        when:
        customerJdbcDao.addCustomer(NEW_CUSTOMER)

        then:
        var sql = """
                SELECT * FROM customer
                WHERE email = ?
                """
        def actual = jdbcTemplate.query(sql, customerJdbcDao.rowMapper, NEW_CUSTOMER.getEmail())
                .stream()
                .findFirst().orElseThrow(() -> new RuntimeException("customer not found"))
        verifyAll(actual) {
            getName() == NEW_CUSTOMER.getName()
            getEmail() == NEW_CUSTOMER.getEmail()
            getAge() == NEW_CUSTOMER.getAge()
            getCreatedAt() != null
        }
    }

    def "should add customer successfully SqlUpdate"() {
        when:
        def generatedId = customerJdbcDao.addCustomerSqlUpdate(NEW_CUSTOMER)

        then:
        var sql = """
                SELECT * FROM customer
                WHERE email = ?
                """
        def actual = jdbcTemplate.query(sql, customerJdbcDao.rowMapper, NEW_CUSTOMER.getEmail())
                .stream()
                .findFirst().orElseThrow(() -> new RuntimeException("customer not found"))
        print(actual)

        verifyAll(actual) {
            getId() == generatedId
            getName() == NEW_CUSTOMER.getName()
            getEmail() == NEW_CUSTOMER.getEmail()
            getAge() == NEW_CUSTOMER.getAge()
            getCreatedAt() != null
        }
    }

    def "should add customer successfully BatchSqlUpdate"() {
        when:
        customerJdbcDao.addCustomerWithPaymentsBatchSqlUpdate(NEW_CUSTOMER)

        then:
        var sql = """
                SELECT * FROM payment
                """
        def actual = jdbcTemplate.query(sql, customerJdbcDao.paymentRowMapper)
        print(actual)
        verifyAll(actual) {
            size() == 10
        }
    }


    def "should not add customer when duplicate email"() {
        when: 'try to add customers with the same email'
        customerJdbcDao.addCustomer(NEW_CUSTOMER)
        customerJdbcDao.addCustomer(NEW_CUSTOMER)

        then:
        thrown(DuplicateKeyException)
    }


    def "should update customer successfully"() {
        when:
        customerJdbcDao.updateCustomer(CUSTOMER.withId(CUSTOMER_ID), NEW_CUSTOMER)

        then:
        var sql = """
                SELECT * FROM customer
                WHERE email = ?
                """
        def actual = jdbcTemplate.query(sql, customerJdbcDao.rowMapper, NEW_CUSTOMER.getEmail())
                .stream()
                .findFirst().orElseThrow(() -> new RuntimeException("customer not found"))
        verifyAll(actual) {
            getName() == NEW_CUSTOMER.getName()
            getEmail() == NEW_CUSTOMER.getEmail()
            getAge() == NEW_CUSTOMER.getAge()
            getCreatedAt() != null
        }
    }

    def "should update customer successfully SqlUpdate"() {
        when:
        customerJdbcDao.updateCustomerSqlUpdate(CUSTOMER.withId(CUSTOMER_ID), NEW_CUSTOMER)

        then:
        var sql = """
                SELECT * FROM customer
                WHERE email = ?
                """
        def actual = jdbcTemplate.query(sql, customerJdbcDao.rowMapper, NEW_CUSTOMER.getEmail())
                .stream()
                .findFirst().orElseThrow(() -> new RuntimeException("customer not found"))
        actual.with {
            getName() == NEW_CUSTOMER.getName()
            getEmail() == NEW_CUSTOMER.getEmail()
            getAge() == NEW_CUSTOMER.getAge()
            getCreatedAt() == NEW_CUSTOMER.getCreatedAt()
        }
    }

    def "should not update customer when duplicate email"() {
        given:
        var sql = """
            INSERT INTO customer(name, email, age, created_at) 
            VALUES(?, ?, ?, ?)
            """
        jdbcTemplate.update(sql,
                NEW_CUSTOMER.getName(),
                NEW_CUSTOMER.getEmail(),
                NEW_CUSTOMER.getAge(),
                CUSTOMER_CREATED_AT)

        when:
        customerJdbcDao.updateCustomer(CUSTOMER.withId(CUSTOMER_ID), NEW_CUSTOMER)

        then:
        thrown(DuplicateKeyException)
    }


    def "should delete customer"() {
        when:
        customerJdbcDao.deleteCustomer(CUSTOMER_ID)

        then:
        var sql = """
                SELECT * FROM customer
                WHERE id = ?
                """
        jdbcTemplate.query(sql, customerJdbcDao.rowMapper, CUSTOMER_ID).isEmpty()
    }

    def "should not delete customer when not exist"() {
        when:
        customerJdbcDao.deleteCustomer(CUSTOMER_ID + 100)

        then:
        thrown(RuntimeException)
    }

}
