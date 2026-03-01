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

        String upRaw = upParts.length > 1 ? upParts[1].trim() : "";
        String downRaw = parts.length > 1 ? parts[1].trim() : "";

        // Strip single-line comments before splitting on semicolons
        // to prevent comments containing semicolons from breaking statement parsing
        String upClean = stripLineComments(upRaw);
        String downClean = stripLineComments(downRaw);

        List<String> upList = Arrays.stream(upClean.split(";"))
                .map(String::trim).filter(s -> !s.isEmpty()).toList();
        List<String> downList = Arrays.stream(downClean.split(";"))
                .map(String::trim).filter(s -> !s.isEmpty()).toList();

        return new MigrationFile(path.getFileName().toString(), tag, upRaw, downRaw, upList, downList);
    }

    private static String extractTag(String content) {
        return Arrays.stream(content.split("\n"))
                .filter(line -> line.trim().startsWith("-- tag:"))
                .findFirst()
                .map(line -> line.replace("-- tag:", "").trim())
                .orElseThrow(() -> new RuntimeException("Missing tag in migration"));
    }

    /**
     * Strip single-line SQL/CQL comments (lines starting with --)
     * before splitting on semicolons. This prevents comments containing
     * semicolons from breaking statement parsing.
     */
    private static String stripLineComments(String sql) {
        return Arrays.stream(sql.split("\n"))
                .filter(line -> !line.trim().startsWith("--"))
                .reduce("", (a, b) -> a + "\n" + b)
                .trim();
    }
}
