package com.dwit.migrator.infra.util;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Objects;

public class FileUtil {

    public static Map<String, Object> loadPropertiesYaml() throws Exception {
        Yaml yaml = new Yaml();

        // Check for external migrator-config.yml first (for twist-hub-be project)
        Path migratorConfig = Paths.get("src/main/resources/migrator-config.yml");
        if (Files.exists(migratorConfig)) {
            try (InputStream input = Files.newInputStream(migratorConfig)) {
                return yaml.load(input);
            }
        }

        // Check for APP_CONFIG_PATH environment variable
        String externalPath = System.getenv("APP_CONFIG_PATH");
        if (externalPath != null) {
            Path configPath = Paths.get(externalPath);
            if (Files.exists(configPath)) {
                try (InputStream input = Files.newInputStream(configPath)) {
                    return yaml.load(input);
                }
            }
        }

        // Fallback to config/application.yml in current directory
        Path configDir = Paths.get("config/application.yml");
        if (Files.exists(configDir)) {
            try (InputStream input = Files.newInputStream(configDir)) {
                return yaml.load(input);
            }
        }

        // Finally, try classpath resource
        try (InputStream input = FileUtil.class.getClassLoader().getResourceAsStream("application.yml")) {
            return yaml.load(input);
        }
    }

    public static String resolveEnv(String raw) {
        if (raw == null) return null;
        if (raw.startsWith("${") && raw.endsWith("}")) {
            String inner = raw.substring(2, raw.length() - 1);
            String[] parts = inner.split(":", 2);
            String envKey = parts[0];
            String defaultVal = parts.length > 1 ? parts[1] : "";
            return System.getenv().getOrDefault(envKey, defaultVal);
        }
        return raw;
    }
    public static boolean isRunningFromJar() {
        String className = FileUtil.class.getName().replace('.', '/') + ".class";
        String classPath = Objects.requireNonNull(FileUtil.class.getClassLoader().getResource(className)).toString();
        return classPath.startsWith("jar:");
    }
}
