
plugins {
    java
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "me.CatsT0day"
version = "1.0.0.10X-build-000-test"

repositories {
    mavenCentral()
    maven {
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }
    maven {
        url = uri("https://oss.sonatype.org/content/groups/public/")
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_16
    targetCompatibility = JavaVersion.VERSION_16
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.16.5-R0.1-SNAPSHOT")
}

tasks.processResources {
    filteringCharset = "UTF-8"
    filesMatching("**/*.yml", "**/*.properties") {
        expand(
            "version" to project.version,
            "name" to project.name
        )
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.shadowJar {
    archiveBaseName.set("CAPI")
    archiveClassifier.set("")
    archiveVersion.set(project.version)
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    manifest {
        attributes(
            mapOf(
                "Main-Class" to "me.CatsT0day.capi.CAPI",
                "Implementation-Title" to project.name,
                "Implementation-Version" to project.version
            )
        )
    }

    exclude("META-INF/*.SF")
    exclude("META-INF/*.DSA")
    exclude("META-INF/*.RSA")
}