import org.gradle.api.tasks.testing.logging.TestLogEvent.*

plugins {
    kotlin("jvm") version Versions.kotlin
    id("org.jetbrains.kotlin.plugin.spring") version Versions.kotlin
    id("org.springframework.boot") version Versions.springBoot
    id("com.diffplug.gradle.spotless") version "3.27.1"
    checkstyle
    jacoco
    pmd
}

apply(plugin = "io.spring.dependency-management")

apply(from = "gradle/generate-openapi.gradle.kts")
apply(from = "gradle/checkstyle.gradle")
apply(from = "gradle/jacoco.gradle")
apply(from = "gradle/lombok.gradle")
apply(from = "gradle/mapstruct.gradle")
apply(from = "gradle/spotless.gradle")

group = "com.johncnstn"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {

    // Common dependencies
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    compileOnly("org.springframework.data:spring-data-commons:${Versions.springDataCommons}")

    implementation("com.fasterxml.jackson.module:jackson-module-afterburner")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.google.guava:guava:${Versions.guava}")
    implementation("com.vladmihalcea:hibernate-types-52:${Versions.hibernateTypes}")
    implementation("io.jsonwebtoken:jjwt-api:${Versions.jjwt}")
    implementation("io.swagger:swagger-annotations:${Versions.swaggerAnnotations}")
    implementation("org.apache.commons:commons-lang3")
    implementation("org.flywaydb:flyway-core:${Versions.flyway}")
    implementation("org.postgresql:postgresql")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-undertow")
    implementation("org.springframework.boot:spring-boot-starter-web") {
        exclude("spring-boot-starter-tomcat")
    }
    implementation("org.yaml:snakeyaml")
    implementation("org.zalando:problem-spring-web:${Versions.problemSpringWeb}")

    runtimeOnly("io.jsonwebtoken:jjwt-impl:${Versions.jjwt}")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:${Versions.jjwt}")
    runtimeOnly("io.springfox:springfox-swagger-ui:${Versions.springfoxSwaggerUi}") {
        exclude("springfox-spring-web")
    }
    runtimeOnly("javax.xml.bind:jaxb-api")

    // Test dependencies
    testImplementation("com.github.javafaker:javafaker:${Versions.javafaker}")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude("org.junit.vintage:junit-vintage-engine")
    }
    testImplementation("org.testcontainers:testcontainers:${Versions.testcontainers}")
}

configurations {
    compileOnly {
        extendsFrom(annotationProcessor.get())
    }
    implementation {
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-tomcat")
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

tasks.bootRun {
    args = listOf("--spring.profiles.active=local")
    jvmArgs = listOf("-server", "-Xms256m", "-Xmx512m", "-Duser.country=US", "-Duser.language=en", "-Duser.timezone=UTC")
}

tasks.test {
    useJUnitPlatform()
    with(testLogging) {
        events = setOf(PASSED, SKIPPED, FAILED)
    }
}
