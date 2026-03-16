plugins {
    alias(libs.plugins.kotlin.jvm)
}

allprojects {
    group = "net.mythicisland.template"
    version = "0.0.1"

    repositories {
        mavenCentral()
        maven("https://buf.build/gen/maven")
        maven("https://repo.simplecloud.app/snapshots")
        maven("https://repo.xxjanisxx.dev/releases")
    }

}

subprojects {
    apply(plugin = "kotlin")
    apply(plugin = "java")

    dependencies {
    }

    tasks.test {
        useJUnitPlatform()
    }

    kotlin {
        jvmToolchain(21)
    }

    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(21))
        withJavadocJar()
        withSourcesJar()
    }

}