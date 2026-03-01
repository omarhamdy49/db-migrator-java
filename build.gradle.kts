plugins {
    id("application")
    java
    `maven-publish`
}

group = "com.dwit.migrator"
version = "1.0.0"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifact(tasks.getByName("jar")) {
                builtBy(tasks.getByName("jar"))
            }

            groupId = "com.dwit.migrator"
            artifactId = "db-migrator"
            version = "1.0.0"
        }
    }

    repositories {
        maven {
            name = "local"
            url = uri(layout.buildDirectory.dir("repo"))
        }
    }
}

application {
    mainClass.set("com.dwit.migrator.MigrationApp")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.postgresql:postgresql:42.7.3")
    implementation("com.mysql:mysql-connector-j:8.3.0")
    implementation("org.apache.cassandra:java-driver-core:4.19.0")
    implementation("org.yaml:snakeyaml:2.0")

    // Use compileOnly for SLF4J API - Spring Boot will provide the implementation
    compileOnly("org.slf4j:slf4j-api:2.0.12")
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "com.dwit.migrator.MigrationApp"
    }

    archiveFileName.set("db-migrator.jar")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    // Include all runtime dependencies EXCEPT SLF4J implementations
    from({
        configurations.runtimeClasspath.get()
            .filter { it.name.endsWith("jar") }
            .filter { !it.name.contains("slf4j-simple") }
            .filter { !it.name.contains("slf4j-nop") }
            .filter { !it.name.contains("slf4j-jdk14") }
            .filter { !it.name.contains("slf4j-log4j") }
            .filter { !it.name.contains("logback") }
            .map { zipTree(it) }
    })

    // Exclude SLF4J binding classes if they somehow get included
    exclude("org/slf4j/impl/**")

    // Include config by default (optional)
    from("src/main/resources") {
        include("application.yml")
    }
}
