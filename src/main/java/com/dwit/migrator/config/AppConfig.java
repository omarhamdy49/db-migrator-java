package com.dwit.migrator.config;

import com.dwit.migrator.MigrationApp;
import com.dwit.migrator.infra.util.LoggerUtil;
import org.slf4j.Logger;
import org.yaml.snakeyaml.Yaml;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class AppConfig {
    private static final Logger log = LoggerUtil.getLogger(MigrationApp.class);
    private static final Map<String, Object> yaml = loadYaml();

    public static String getDefaultEngine() {
        return getEnvOrDefault("DB_DEFAULT", "postgres");
    }

    public static Path migrationsDir(String engine) {
        boolean runningFromJar = Objects.requireNonNull(AppConfig.class.getResource("")).toString().startsWith("jar:");

        if (runningFromJar) {
            // Running from a built JAR: use current working directory
            return Paths.get(System.getProperty("user.dir"), "migrations", engine);
        } else {
            // Development environment: use src/resources
            return Paths.get("src", "main", "resources", "migrations", engine);
        }
    }

    public static String getEnvOrDefault(String key, String defaultValue) {
        return Optional.ofNullable(System.getenv(key.toUpperCase()))
                .or(() -> Optional.ofNullable((String) yaml.get(key)))
                .orElse(defaultValue);
    }

    private static Map<String, Object> loadYaml() {
        String externalPath = System.getenv("APP_CONFIG_PATH");
        Path configPath = externalPath != null ? Paths.get(externalPath) : Paths.get("config/application.yml");

        try {
            if (Files.exists(configPath)) {
                try (InputStream in = Files.newInputStream(configPath)) {
                    return new Yaml().load(in);
                }
            } else {
                try (InputStream in = AppConfig.class.getClassLoader().getResourceAsStream("application.yml")) {
                    if (in == null) throw new FileNotFoundException("application.yml not found in classpath.");
                    return new Yaml().load(in);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load configuration", e);
        }
    }
}