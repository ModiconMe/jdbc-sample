package io.modicon.app.infrastructure.repository;

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.SqlUpdate;

import javax.sql.DataSource;
import java.sql.Types;

public class UpdateCustomer extends SqlUpdate {

    private static final String SQL = "UPDATE customer SET name = :name, email = :email, age = :age WHERE id = :id";

    public UpdateCustomer(DataSource dataSource) {
        super(dataSource, SQL);
        super.declareParameter(new SqlParameter("name", Types.VARCHAR));
        super.declareParameter(new SqlParameter("email", Types.VARCHAR));
        super.declareParameter(new SqlParameter("age", Types.INTEGER));
        super.declareParameter(new SqlParameter("id", Types.BIGINT));
    }
}
