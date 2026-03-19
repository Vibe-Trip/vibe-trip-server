plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    kotlin("plugin.jpa")
    kotlin("kapt")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("org.jlleitschuh.gradle.ktlint") version "12.1.1"
}

val projectGroup: String by project
val applicationVersion: String by project
val jdkVersion: String by project
val kotestVersion: String by project
val mockKVersion: String by project
val queryDslVersion: String by project

group = projectGroup
version = applicationVersion
description = "VibeTripServer"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(jdkVersion)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("tools.jackson.module:jackson-module-kotlin")

    // Web
    implementation("org.springframework.boot:spring-boot-starter-webmvc")

    // Validation
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // DB
    runtimeOnly("com.h2database:h2")
    implementation("org.springframework.boot:spring-boot-h2console")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("com.mysql:mysql-connector-j")
    implementation("io.github.openfeign.querydsl:querydsl-core:${queryDslVersion}")
    implementation("io.github.openfeign.querydsl:querydsl-jpa:$queryDslVersion")
    kapt("io.github.openfeign.querydsl:querydsl-apt:$queryDslVersion:jpa")

    // Cache
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("com.github.ben-manes.caffeine:caffeine:3.2.3")

    // Metric
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    runtimeOnly("io.micrometer:micrometer-registry-prometheus")

    // Log
    implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")

    // Api Docs
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.2")

    // Security
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")

    // JWT
    implementation("io.jsonwebtoken:jjwt-api:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test")
    testImplementation("org.springframework.boot:spring-boot-starter-validation-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("io.kotest:kotest-runner-junit5-jvm:${kotestVersion}")
    testImplementation("io.kotest:kotest-assertions-core-jvm:${kotestVersion}")
    testImplementation("io.mockk:mockk:${mockKVersion}")}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
    }
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

// Querydsl Settings
val kaptGeneratedDir = "build/generated/source/kapt/main"

kapt {
    keepJavacAnnotationProcessors = true
    arguments {
        arg("querydsl.entityAccessors", "true")
    }
}


sourceSets {
    main {
        java.srcDirs(kaptGeneratedDir)
    }
}

tasks.named("clean") {
    doLast {
        file(kaptGeneratedDir).deleteRecursively()
    }
}
