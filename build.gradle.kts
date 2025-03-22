import org.jetbrains.kotlin.gradle.internal.config.LanguageFeature

plugins {
    kotlin("jvm") version "2.1.20"
    alias(libs.plugins.versions)
    alias(libs.plugins.versions.filter)
    alias(libs.plugins.versions.update)
}

version = "2019"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(libs.kotest.assertions.core)
    implementation(libs.junit.jupiter)
    implementation(libs.junit.jupiter.api)
    implementation(libs.junit.jupiter.engine)
    implementation(libs.junit.platform.launcher)
    implementation(libs.kotlin.serialization)
}

tasks.test {
    useJUnitPlatform()
    minHeapSize = "1g"
    maxHeapSize = "30g"
    testLogging.showStandardStreams = true
    filter {
        setIncludePatterns("*Test", "Day*")
    }
}

kotlin {
    jvmToolchain(21)
    @Suppress("UnsafeCompilerArguments")
    sourceSets.all {
        languageSettings {
            enableLanguageFeature(LanguageFeature.WhenGuards.name)
            enableLanguageFeature(LanguageFeature.MultiDollarInterpolation.name)
            enableLanguageFeature(LanguageFeature.BreakContinueInInlineLambdas.name)
        }
    }
    compilerOptions {
        suppressWarnings = true
    }
}
