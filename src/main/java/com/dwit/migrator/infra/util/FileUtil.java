package com.dwit.migrator.infra.util;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;
import java.util.Objects;

public class FileUtil {

    public static Map<String, Object> loadPropertiesYaml() throws Exception {
        Yaml yaml = new Yaml();
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
