package com.dwit.migrator.core;

import com.dwit.migrator.config.AppConfig;
import com.dwit.migrator.domain.IMigrationService;
import com.dwit.migrator.infra.adapters.cassandra.CassandraMigrationService;
import com.dwit.migrator.infra.adapters.mysql.MySQLMigrationService;
import com.dwit.migrator.infra.adapters.postgres.PostgresMigrationService;

import java.util.Map;

public class MigrationRunner {
    private final Map<String, IMigrationService> engines;

    public MigrationRunner() {
        engines = Map.of(
                "postgres", new PostgresMigrationService(),
                "mysql", new MySQLMigrationService(),
                "cassandra", new CassandraMigrationService()
        );
    }

    private IMigrationService getEngine(String engine) {
        String key = engine == null || engine.isEmpty()
                ? AppConfig.getDefaultEngine()
                : engine.toLowerCase();
        if (!engines.containsKey(key)) throw new RuntimeException("Unsupported engine: " + key);
        return engines.get(key);
    }

    public void migrate(String engine) throws Exception {
        getEngine(engine).migrateAll();
    }

    public void rollbackTag(String engine, String tag) throws Exception {
        getEngine(engine).rollbackByTag(tag);
    }

    public void rollbackFile(String engine, String filename) throws Exception {
        getEngine(engine).rollbackByFileName(filename);
    }

    public void checkConnection(String engine) throws Exception {
        getEngine(engine).checkConnection();

    }
}