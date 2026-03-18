import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("maven-publish")
}

java {
    withJavadocJar()
    withSourcesJar()
}

dependencies {
    api(libs.bundles.grpc)
    api(libs.jnats)
}

tasks.named<ShadowJar>("shadowJar") {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}