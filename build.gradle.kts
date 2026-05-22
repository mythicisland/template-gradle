import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.shadow)
}

allprojects {
    group = "net.mythicisland.template"
    version = "1.0.0"

    repositories {
        mavenCentral()
        maven("https://repo.simplecloud.app/snapshots")
        maven("https://repo.xxjanisxx.dev/releases")
        maven("https://repo.xxjanisxx.dev/snapshots")
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://buf.build/gen/maven")
    }
}

subprojects {
    apply {
        plugin("kotlin")
        plugin("java")
        plugin("com.gradleup.shadow")
    }

    dependencies {
        testImplementation(rootProject.libs.kotlin.test)
        implementation(rootProject.libs.kotlinx.coroutines.core)
    }

    kotlin {
        jvmToolchain(21)
        compilerOptions {
            apiVersion = KotlinVersion.KOTLIN_2_3
            jvmTarget = JvmTarget.JVM_21
            freeCompilerArgs = listOf("-Xannotation-default-target=param-property")
        }
    }

    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(21))
    }

    tasks.test {
        useJUnitPlatform()
    }

    tasks.shadowJar {
        mergeServiceFiles()
        archiveFileName.set("${project.name}.jar")
    }
}