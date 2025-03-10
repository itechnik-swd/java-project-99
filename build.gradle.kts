import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    application
    checkstyle
    jacoco
    id("io.freefair.lombok") version "8.12.1"
    id("org.springframework.boot") version "3.4.3"
    id("io.spring.dependency-management") version "1.1.7"
    id("com.github.ben-manes.versions") version "0.52.0"
    id("io.sentry.jvm.gradle") version "5.3.0"
}

group = "hexlet.code"
version = "0.0.1-SNAPSHOT"

application {
    mainClass = "hexlet.code.AppApplication"
}

jacoco {
    toolVersion = "0.8.10"
}

repositories {
    mavenCentral()
}

dependencies {
    // Для работы с БД
    runtimeOnly("com.h2database:h2:2.2.224")

    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-devtools")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-configuration-processor")

    // Зависимость нужна для работы механизма Spring Data JPA
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // Зависимость нужна для работы механизма Spring Security
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")

    // Библиотека для автоматического маппинга (конвертации сущностей в DTO и обратно)
    implementation("org.mapstruct:mapstruct:1.5.5.Final")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")

    // Модуль для частичного обновления объектов
    implementation("org.openapitools:jackson-databind-nullable:0.2.6")

    // Зависимость нужна для работы механизма Spring Data JPA в тестах.
    // Позволяет автоматически создавать объекты на основе сущностей и сохранять их в базу.
    implementation("net.datafaker:datafaker:2.4.2")
    implementation("org.instancio:instancio-junit:3.6.0")

    // Для работы с Swagger
    // https://mvnrepository.com/artifact/org.springdoc/springdoc-openapi-starter-webmvc-ui
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.5")

    // Для работы с OpenTelemetry (sentry-opentelemetry-agent)
    implementation("io.sentry:sentry-opentelemetry-agent:8.3.0")

    // implementation("org.mockito:mockito-core:5.15.2")

    // Для работы с тестами
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    // Для работы с JSON (проверка тела ответа в тестах)
    testImplementation("net.javacrumbs.json-unit:json-unit-assertj:3.2.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        exceptionFormat = TestExceptionFormat.FULL
        events = mutableSetOf(TestLogEvent.FAILED, TestLogEvent.PASSED, TestLogEvent.SKIPPED)
        showStandardStreams = true
    }
}

tasks.jacocoTestReport { reports { xml.required.set(true) } }

sentry {
    // Generates a JVM (Java, Kotlin, etc.) source bundle and uploads your source code to Sentry.
    // This enables source context, allowing you to see your source
    // code as part of your stack traces in Sentry.
    includeSourceContext = true

    org = "itechnik-swd"
    projectName = "java-spring-boot"
    authToken = System.getenv("SENTRY_AUTH_TOKEN")
}

tasks.sentryBundleSourcesJava {
    enabled = System.getenv("SENTRY_AUTH_TOKEN") != null
}