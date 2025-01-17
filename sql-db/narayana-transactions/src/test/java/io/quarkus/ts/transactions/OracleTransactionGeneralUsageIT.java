package io.quarkus.ts.transactions;

import io.quarkus.test.bootstrap.OracleService;
import io.quarkus.test.bootstrap.RestService;
import io.quarkus.test.scenarios.QuarkusScenario;
import io.quarkus.test.services.Container;
import io.quarkus.test.services.QuarkusApplication;
import io.quarkus.ts.transactions.recovery.TransactionExecutor;

@QuarkusScenario
public class OracleTransactionGeneralUsageIT extends TransactionCommons {

    static final int ORACLE_PORT = 1521;

    @Container(image = "${oracle.image}", port = ORACLE_PORT, expectedLog = "DATABASE IS READY TO USE!")
    static OracleService database = new OracleService();

    @QuarkusApplication
    static RestService app = new RestService().withProperties("oracle.properties")
            .withProperty("quarkus.otel.exporter.otlp.traces.endpoint", jaeger::getCollectorUrl)
            .withProperty("quarkus.datasource.username", database.getUser())
            .withProperty("quarkus.datasource.password", database.getPassword())
            .withProperty("quarkus.datasource.jdbc.url", database::getJdbcUrl);

    @Override
    protected RestService getApp() {
        return app;
    }

    @Override
    protected TransactionExecutor getTransactionExecutorUsedForRecovery() {
        return TransactionExecutor.QUARKUS_TRANSACTION_CALL;
    }

    @Override
    protected String[] getExpectedJdbcOperationNames() {
        return new String[] { "SELECT mydb", "INSERT mydb.journal", "UPDATE mydb.account" };
    }

    @Override
    protected void testTransactionRecoveryInternal() {
        // disables transaction recovery test for Oracle due to upstream issue
        // TODO: remove this method when https://github.com/quarkusio/quarkus/issues/35333 gets fixed
    }
}
