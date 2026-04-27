plugins {
    java
    `maven-publish`
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "com.github.CatsT0day"
version = "1.0.0.6"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.release.set(16)
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://jitpack.io")
    maven("https://lucko.me")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.17.1-R0.1-SNAPSHOT")
    implementation("org.reflections:reflections:0.10.2")
    compileOnly("net.luckperms:api:5.4")
}

tasks.shadowJar {
    archiveBaseName.set("EclipseAPI")
    archiveClassifier.set("")
    archiveVersion.set(project.version.toString())
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

configure<PublishingExtension> {
    publications {
        register<MavenPublication>("maven") {
            groupId = "com.github.CatsT0day"
            artifactId = "EclipseAPI"
            version = project.version.toString()

            artifact(tasks.shadowJar)
        }
    }
}
