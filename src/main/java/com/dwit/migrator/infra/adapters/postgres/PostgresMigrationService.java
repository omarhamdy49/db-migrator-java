package com.dwit.migrator.infra.adapters.postgres;

import com.dwit.migrator.config.AppConfig;
import com.dwit.migrator.domain.IMigrationService;
import com.dwit.migrator.domain.MigrationFile;
import com.dwit.migrator.infra.MigrationParser;
import com.dwit.migrator.infra.util.LoggerUtil;
import org.slf4j.Logger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.Objects;

public class PostgresMigrationService implements IMigrationService {
    private static final Logger log = LoggerUtil.getLogger(PostgresMigrationService.class);
    private final Path migrationsDir;
    private final String ENGINE_NAME = "postgres";

    public PostgresMigrationService() {
        this.migrationsDir =Objects.requireNonNull(AppConfig.migrationsDir(ENGINE_NAME));
    }

    @Override
    public void checkConnection() throws Exception {
        try (Connection conn = PostgresDBContext.getConnection()) {
            //create migration table if not exists
            ensureMigrationsTableExists(conn);
            log.info("✅ Connection OK to {}", ENGINE_NAME);
        }
    }

    public void migrateAll() throws Exception {
        try (Connection conn = PostgresDBContext.getConnection()) {
            Files.list(migrationsDir).filter(f -> f.toString().endsWith(".sql")).forEach(file -> {
                try {
                    MigrationFile mf = MigrationParser.parse(file);
                    PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM migrations WHERE name = ? AND status = 'applied'");
                    ps.setString(1, mf.name);
                    ResultSet rs = ps.executeQuery();
                    if (!rs.next()) {
                        try {
                            conn.createStatement().execute(mf.upSql);
                            PreparedStatement insert = conn.prepareStatement("INSERT INTO migrations (name, tag, status) VALUES (?, ?, 'applied')");
                            insert.setString(1, mf.name);
                            insert.setString(2, mf.tag);
                            insert.execute();
                            System.out.println("✅ Applied: " + mf.name);
                        } catch (Exception sqlEx) {
                            System.err.println("\n❌ Migration failed!");
                            System.err.println("File: " + file.getFileName());
                            System.err.println("Migration name: " + mf.name);
                            System.err.println("--- SQL ---\n" + mf.upSql + "\n---");
                            if (sqlEx instanceof SQLException) {
                                SQLException sex = (SQLException) sqlEx;
                                System.err.println("SQL Error: " + sex.getMessage());
                                System.err.println("SQL State: " + sex.getSQLState());
                                System.err.println("Error Code: " + sex.getErrorCode());
                            } else {
                                System.err.println("Error: " + sqlEx.getMessage());
                            }
                            throw new RuntimeException("PostgreSQL migration failed for file: " + file.getFileName(), sqlEx);
                        }
                    }
                } catch (Exception e) {
                    throw new RuntimeException("PostgreSQL migration failed during migration file processing: " + file.getFileName(), e);
                }
            });
        }
    }

    public void rollbackByTag(String tag) throws Exception {
        try (Connection conn = PostgresDBContext.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("SELECT name FROM migrations WHERE tag = ? AND status = 'applied'");
            ps.setString(1, tag);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) throw new RuntimeException("Migration not applied: " + tag);
            Path file = migrationsDir.resolve(rs.getString("name"));
            MigrationFile mf = MigrationParser.parse(file);
            conn.createStatement().execute(mf.downSql);
            conn.prepareStatement("UPDATE migrations SET status = 'rolled_back' WHERE tag = ?").executeUpdate();
            System.out.println("✅ Rolled back tag: " + tag);
        }
    }

    public void rollbackByFileName(String fileName) throws Exception {
        try (Connection conn = PostgresDBContext.getConnection()) {
            Path file = migrationsDir.resolve(fileName);
            MigrationFile mf = MigrationParser.parse(file);
            PreparedStatement ps = conn.prepareStatement("SELECT tag FROM migrations WHERE name = ? AND status = 'applied'");
            ps.setString(1, fileName);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) throw new RuntimeException("Migration not applied: " + fileName);
            String tag = rs.getString("tag");
            conn.createStatement().execute(mf.downSql);
            conn.prepareStatement("UPDATE migrations SET status = 'rolled_back' WHERE tag = ?").executeUpdate();
            System.out.println("✅ Rolled back file: " + fileName);
        }
    }

    private void ensureMigrationsTableExists(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS migrations (tag VARCHAR(32) , name VARCHAR(255), applied_at TIMESTAMP DEFAULT NOW(), status VARCHAR(20));");
        }
    }
}