package pl.kmolski;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.dbunit.DataSourceBasedDBTestCase;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.ext.postgresql.PostgresqlDataTypeFactory;

import javax.sql.DataSource;

public class DbunitProxyReproTest extends DataSourceBasedDBTestCase {

    @Override
    protected DataSource getDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:tc:postgresql:14.2:///test?TC_INITSCRIPT=init.sql");
        return new HikariDataSource(config);
    }

    @Override
    protected void setUpDatabaseConfig(DatabaseConfig config) {
        super.setUpDatabaseConfig(config);
        config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new PostgresqlDataTypeFactory());
    }

    @Override
    protected IDataSet getDataSet() throws Exception {
        return new FlatXmlDataSetBuilder().build(getClass().getClassLoader().getResourceAsStream("data.xml"));
    }

    public void testWithHikariDataSource() throws Exception {
        // throws java.lang.ClassCastException: class com.zaxxer.hikari.pool.HikariProxyConnection cannot be cast to class org.postgresql.PGConnection
        //
        // at org.dbunit.ext.postgresql.PostgreSQLOidDataType.setSqlValue(PostgreSQLOidDataType.java:93)
        // at org.dbunit.database.statement.SimplePreparedStatement.addValue(SimplePreparedStatement.java:78)
        // at org.dbunit.database.statement.AutomaticPreparedBatchStatement.addValue(AutomaticPreparedBatchStatement.java:63)

        IDataSet dataset = getConnection().createDataSet();
        assertEquals(1, dataset.getTable("foo").getRowCount());
    }
}
