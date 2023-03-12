package io.modicon.app.infrastructure.repository;

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.SqlUpdate;

import javax.sql.DataSource;
import java.sql.Types;

public class InsertCustomer extends SqlUpdate {

    private static final String SQL = """
                INSERT INTO customer(name, email, age, created_at)
                VALUES(:name, :email, :age, :created_at)
                """;

    public InsertCustomer(DataSource dataSource) {
        super(dataSource, SQL);
        super.declareParameter(new SqlParameter("name", Types.VARCHAR));
        super.declareParameter(new SqlParameter("email", Types.VARCHAR));
        super.declareParameter(new SqlParameter("age", Types.INTEGER));
        super.declareParameter(new SqlParameter("created_at", Types.TIMESTAMP));
        super.setGeneratedKeysColumnNames("id");
        super.setReturnGeneratedKeys(true);
    }
}
