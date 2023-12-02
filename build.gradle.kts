import java.nio.charset.StandardCharsets

plugins {
    idea
    `java-library`
    `jvm-test-suite`
    jacoco
    `maven-publish`
}

group = "io.github.mishyy"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

configure<JavaPluginExtension> {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21

    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }

    withJavadocJar()
    withSourcesJar()
}

configure<TestingExtension> {
    suites {
        withType<JvmTestSuite> {
            useJUnitJupiter()
        }
    }
}

configure<PublishingExtension> {
    publications {
        register<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
}

tasks {
    withType<JavaCompile> {
        options.encoding = StandardCharsets.UTF_8.name()
        options.isFork = true
        options.release.set(21)

        options.compilerArgs.addAll(listOf("-parameters", "-Xlint:all,-processing,-path,-fallthrough,-serial"))
    }

    withType<Javadoc> {
        options {
            encoding(StandardCharsets.UTF_8.name()).quiet()

            this as StandardJavadocDocletOptions
            addBooleanOption("notimestamp", true)
            addBooleanOption("linksource", true)
            addBooleanOption("Xdoclint:all,-missing", true)
        }
    }

    withType<ProcessResources> {
        filteringCharset = StandardCharsets.UTF_8.name()
    }
}
