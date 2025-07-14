package com.dwit.migrator.infra;

import com.dwit.migrator.domain.MigrationFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class MigrationParser {
    public static MigrationFile parse(Path path) throws IOException {
        String content = Files.readString(path);
        String tag = extractTag(content);

        String[] parts = content.split("-- down", 2);
        String[] upParts = parts[0].split("-- up", 2);

        String up = upParts.length > 1 ? upParts[1].trim() : "";
        String down = parts.length > 1 ? parts[1].trim() : "";

        List<String> upList = Arrays.stream(up.split(";"))
                .map(String::trim).filter(s -> !s.isEmpty()).toList();
        List<String> downList = Arrays.stream(down.split(";"))
                .map(String::trim).filter(s -> !s.isEmpty()).toList();

        return new MigrationFile(path.getFileName().toString(), tag, up, down, upList, downList);
    }

    private static String extractTag(String content) {
        return Arrays.stream(content.split("\n"))
                .filter(line -> line.trim().startsWith("-- tag:"))
                .findFirst()
                .map(line -> line.replace("-- tag:", "").trim())
                .orElseThrow(() -> new RuntimeException("Missing tag in migration"));
    }
}
