package io.modicon.app.infrastructure.repository;

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.BatchSqlUpdate;

import javax.sql.DataSource;
import java.sql.Types;

public class BatchSqlInsertCustomerPayments extends BatchSqlUpdate {

    private static final String SQL = """
                INSERT INTO payment(customer_id, amount)
                VALUES(:customer_id, :amount)
                """;
    private static final int BATCH_SIZE = 2;

    public BatchSqlInsertCustomerPayments(DataSource dataSource) {
        super(dataSource, SQL);
        super.declareParameter(new SqlParameter("customer_id", Types.INTEGER));
        super.declareParameter(new SqlParameter("amount", Types.BIGINT));
        super.setBatchSize(BATCH_SIZE);
    }
}
