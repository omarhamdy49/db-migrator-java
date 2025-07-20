plugins {
    id("application")
    java
    `maven-publish`
}

group = "com.dwit.migrator"
version = "1.0.0"

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
            url = uri("$buildDir/repo")
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
    implementation("org.apache.cassandra:java-driver-core:4.19.0")
    implementation("org.yaml:snakeyaml:2.0")
    implementation ("org.slf4j:slf4j-simple:2.0.12")
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "com.dwit.migrator.MigrationApp"
    }

    archiveFileName.set("db-migrator.jar") // <-- ✅ Your custom JAR name
    // Include all runtime dependencies in the jar (fat JAR)
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from({
        configurations.runtimeClasspath.get()
            .filter { it.name.endsWith("jar") }
            .map { zipTree(it) }
    })

    // Include config by default (optional)
    from("src/main/resources") {
        include("application.yml")
    }
}
