package org.opentcs.web.db;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 使用 Testcontainers 在真实 MySQL 上验证 Flyway 迁移链可执行。
 */
@Testcontainers
class FlywayMigrationIT {

    @Container
    static final MySQLContainer<?> MYSQL = new MySQLContainer<>("mysql:8.0")
        .withDatabaseName("opentcsplus")
        .withUsername("test")
        .withPassword("test");

    @Test
    void flywayMigrationsApplySuccessfully() throws Exception {
        Path projectRoot = Path.of(System.getProperty("user.dir")).getParent();

        Flyway.configure()
            .dataSource(MYSQL.getJdbcUrl(), MYSQL.getUsername(), MYSQL.getPassword())
            .locations(
                "filesystem:" + projectRoot.resolve("db/migration"),
                "filesystem:" + projectRoot.resolve("db/repeatable")
            )
            .load()
            .migrate();

        try (Connection conn = DriverManager.getConnection(
            MYSQL.getJdbcUrl(), MYSQL.getUsername(), MYSQL.getPassword())) {
            assertTableExists(conn, "flyway_schema_history");
            assertTableExists(conn, "sys_user");
            assertTableExists(conn, "tcs_vehicle_type");
        }
    }

    private static void assertTableExists(Connection conn, String table) throws Exception {
        try (ResultSet rs = conn.getMetaData().getTables(null, conn.getCatalog(), table, new String[]{"TABLE"})) {
            assertTrue(rs.next(), "Expected table: " + table);
        }
    }
}
