package com.dwit.migrator.infra.adapters.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;
import com.dwit.migrator.config.AppConfig;
import com.dwit.migrator.domain.IMigrationService;
import com.dwit.migrator.domain.MigrationFile;
import com.dwit.migrator.infra.MigrationParser;
import com.dwit.migrator.infra.util.LoggerUtil;
import org.slf4j.Logger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class CassandraMigrationService implements IMigrationService {
    private static final Logger log = LoggerUtil.getLogger(CassandraMigrationService.class);
    private final Path migrationsDir;
    private final String ENGINE_NAME = "cassandra";

    public CassandraMigrationService() {
        this.migrationsDir =Objects.requireNonNull(AppConfig.migrationsDir(ENGINE_NAME));
    }

    @Override
    public void checkConnection() throws Exception {
        try (CqlSession session = CassandraDBContext.getCassandraSession()) {
            //create keyspace if not exists
            ensureKeyspaceExists(session);
            //create migration table if not exists
            ensureCassandraMigrationsTableExists(session);
            log.info("✅ Connection OK to: {}", ENGINE_NAME);
        }
    }

    public void migrateAll() throws Exception {
        try (CqlSession session = CassandraDBContext.cqlKeySpaceSession()) {
            Files.list(migrationsDir).filter(f -> f.toString().endsWith(".cql")).forEach(file -> {
                try {
                    MigrationFile mf = MigrationParser.parse(file);
                    var res = session.execute("SELECT tag FROM migrations WHERE tag = ? AND status = 'applied' ALLOW FILTERING", mf.tag);
                    if (res.one() == null) {
                        for (String q : mf.upSqlList) session.execute(q);
                        session.execute("INSERT INTO migrations (tag, name, status) VALUES (?, ?, 'applied')", mf.tag, mf.name);
                        System.out.println("✅ Applied: " + mf.name);
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Cassandra migration failed", e);
                }
            });
        }
    }

    public void rollbackByTag(String tag) throws Exception {
        try (CqlSession session = CassandraDBContext.cqlKeySpaceSession()) {
            var rs = session.execute("SELECT name FROM migrations WHERE tag = ? AND status = 'applied' ALLOW FILTERING", tag);
            var row = rs.one();
            if (row == null) throw new RuntimeException("Migration not applied: " + tag);
            Path file = migrationsDir.resolve(row.getString("name"));
            MigrationFile mf = MigrationParser.parse(file);
            for (String q : mf.downSqlList) session.execute(q);
            session.execute("UPDATE migrations SET status = 'rolled_back' WHERE tag = ?", tag);
            System.out.println("✅ Rolled back tag: " + tag);
        }
    }

    public void rollbackByFileName(String fileName) throws Exception {
        try (CqlSession session = CassandraDBContext.cqlKeySpaceSession()) {
            Path file = migrationsDir.resolve(fileName);
            MigrationFile mf = MigrationParser.parse(file);
            var rs = session.execute("SELECT tag FROM migrations WHERE name = ? AND status = 'applied' ALLOW FILTERING", fileName);
            var row = rs.one();
            if (row == null) throw new RuntimeException("Migration not applied: " + fileName);
            for (String q : mf.downSqlList) session.execute(q);
            session.execute("UPDATE migrations SET status = 'rolled_back' WHERE tag = ?", row.getString("tag"));
            System.out.println("✅ Rolled back file: " + fileName);
        }
    }

    private void ensureKeyspaceExists(CqlSession session) {
        String keyspace = CassandraDBContext.cassandraKeyspace();
        String query = String.format("""
                    CREATE KEYSPACE IF NOT EXISTS %s
                    WITH replication = {
                        'class': 'SimpleStrategy',
                        'replication_factor': 1
                    } AND durable_writes = true;
                """, keyspace);

        session.execute(query);
        session.execute("USE " + keyspace);

    }

    private void ensureCassandraMigrationsTableExists(CqlSession session) {
        session.execute("""
                    CREATE TABLE IF NOT EXISTS migrations (
                        tag text PRIMARY KEY,
                        name text,
                        applied_at timestamp,
                        status text
                    );
                """);
    }
}