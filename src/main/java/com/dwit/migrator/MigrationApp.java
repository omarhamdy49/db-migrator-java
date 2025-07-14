package com.dwit.migrator;

import com.dwit.migrator.config.AppConfig;
import com.dwit.migrator.core.MigrationRunner;
import com.dwit.migrator.infra.MigrationGenerator;
import com.dwit.migrator.infra.util.LoggerUtil;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class MigrationApp {
    private static final Logger log = LoggerUtil.getLogger(MigrationApp.class);

    public static void main(String[] args) throws Exception {
        MigrationRunner runner = new MigrationRunner();

        if (args.length == 0) {
            printUsageAndExit();
        }

        String cmd = args[0];
        Map<String, String> flags = parseNamedArgs(args);

        String engine = flags.getOrDefault("engine", AppConfig.getDefaultEngine());
        String name = flags.get("name");
        String tag = flags.get("tag");
        String file = flags.get("file");

        log.info("Command: " + cmd + ", Engine: " + engine);

        switch (cmd) {
            case "check" -> runner.checkConnection(engine);
            case "migrate" -> runner.migrate(engine);

            case "rollback-tag" -> {
                if (tag == null && name == null) {
                    System.err.println("❌ Must provide --tag=<value>");
                    printUsageAndExit();
                }
                runner.rollbackTag(engine, tag != null ? tag : name);
            }

            case "rollback-file" -> {
                if (file == null && name == null) {
                    System.err.println("❌ Must provide --file=<filename>");
                    printUsageAndExit();
                }
                runner.rollbackFile(engine, file != null ? file : name);
            }

            case "make" -> {
                if (name == null) {
                    System.err.println("❌ Must provide --name=<migration_name>");
                    printUsageAndExit();
                }
                MigrationGenerator.generate(name, engine);
            }

            default -> printUsageAndExit();
        }
    }

    private static Map<String, String> parseNamedArgs(String[] args) {
        Map<String, String> map = new HashMap<>();
        for (String arg : args) {
            if (arg.startsWith("--")) {
                String[] parts = arg.substring(2).split("=", 2);
                if (parts.length == 2) {
                    map.put(parts[0], parts[1]);
                }
            }
        }
        return map;
    }

    private static void printUsageAndExit() {
        System.out.println("\n❌ Invalid usage.\n");
        System.out.println("USAGE:");
        System.out.println("  migrate [--engine=postgres]");
        System.out.println("  rollback-tag --tag=<tag> [--engine=cassandra]");
        System.out.println("  rollback-file --file=<filename> [--engine=mysql]");
        System.out.println("  make --name=<migration_name> [--engine=postgres]");
        System.out.println();
        System.out.println("FLAGS:");
        System.out.println("  --engine=postgres      Target DB engine (postgres, mysql, cassandra)");
        System.out.println("  --tag=202507123000     Tag of migration to rollback");
        System.out.println("  --file=...             File name to rollback");
        System.out.println("  --name=...             Name of migration to create");
        System.out.println();
        System.out.println("EXAMPLES:");
        System.out.println("  ./gradlew run --args=\"migrate --engine=postgres\"");
        System.out.println("  ./gradlew run --args=\"rollback-tag --tag=202507123456 --engine=mysql\"");
        System.out.println("  ./gradlew run --args=\"make --name=create_users_table --engine=cassandra\"");
        System.exit(1);
    }
}