plugins {
    java
    id("com.github.johnrengelman.shadow") version "8.1.1"
    `maven-publish`
}

group = "me.CatsT0day"
version = "1.0.0.3"

sourceSets {
    main {
        java {
            setSrcDirs(listOf("src/main/java"))
        }
        resources {
            setSrcDirs(listOf("src/main/resources"))
        }
    }
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
    maven {
        url = uri("https://oss.sonatype.org/content/groups/public/")
    }
    maven {
        url = uri("https://ci.ender.zone/plugin/repository/snapshots/")
    }
    maven {
        url = uri("https://repo.lucko.me/")
    }
}


java {
    sourceCompatibility = JavaVersion.VERSION_16
    targetCompatibility = JavaVersion.VERSION_16
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.17.1-R0.1-SNAPSHOT")
    implementation("org.reflections:reflections:0.10.2")
    compileOnly("net.luckperms:api:4.4") {
        isTransitive = false
    }
    compileOnly("net.luckperms:api:5.4") {
        isTransitive = false

    }
}

tasks.processResources {
    filteringCharset = "UTF-8"
    filesMatching("**/*.yml") {
        expand(
            "version" to project.version,
            "name" to project.name
        )
    }
    filesMatching("**/*.properties") {
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
    archiveBaseName = "EclipseApi" +
            ""
    archiveClassifier = ""
    archiveVersion = project.version.toString()
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    manifest {
        attributes(
            mapOf(
                "Main-Class" to "me.CatsT0day.Eclipse.Eclipse",
                "Implementation-Title" to project.name,
                "Implementation-Version" to project.version
            )
        )
    }

    exclude("META-INF/*.SF")
    exclude("META-INF/*.DSA")
    exclude("META-INF/*.RSA")
}