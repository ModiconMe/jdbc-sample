package io.modicon.app.infrastructure.repository;

import io.modicon.app.domain.model.Customer;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.MappingSqlQuery;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class FindCustomerByEmail extends MappingSqlQuery<Customer> {

    private static final String SQL = """
                SELECT * FROM customer
                WHERE email = :email
                """;

    public FindCustomerByEmail(DataSource dataSource) {
        super(dataSource, SQL);
        super.declareParameter(new SqlParameter("email", Types.VARCHAR));
    }

    @Override
    protected Customer mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Customer.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .email(rs.getString("email"))
                .age(rs.getInt("age"))
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                .build();
    }
}
