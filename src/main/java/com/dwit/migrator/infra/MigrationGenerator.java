package com.dwit.migrator.infra;

import com.dwit.migrator.config.AppConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MigrationGenerator {
    public static void generate(String name, String engine) throws IOException {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String ext = engine.equals("cassandra") ? ".cql" : ".sql";
        String filename = timestamp + "__" + name + ext;

        Path folder = AppConfig.migrationsDir(engine);
        Files.createDirectories(folder);

        Path file = folder.resolve(filename);
        String content = "-- tag: " + timestamp + "\n\n-- up\n\n-- down";
        Files.writeString(file, content);
        System.out.println("✅ Created migration: " + file);
    }
}
