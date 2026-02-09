import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.shadow)
    `maven-publish`
}

dependencies {
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.commons.io)
    implementation(libs.gson)
    implementation(libs.caffeine)
    implementation(libs.okhttp)
    implementation(libs.jnats)
    implementation(libs.cloud.api)
    implementation(libs.bundles.logging)
    implementation(libs.bundles.adventure)
    implementation(libs.bundles.grpc)
}

tasks.named<ShadowJar>("shadowJar") {
    mergeServiceFiles()
    archiveClassifier.set("")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifact(tasks.named<ShadowJar>("shadowJar")) {
                classifier = ""
            }
            artifact(tasks.named<Jar>("javadocJar"))
            artifact(tasks.named<Jar>("sourcesJar"))

            pom {
                name.set("Template API")
                description.set("Example API Project")
                url.set("https://github.com/mythicisland/template-gradle")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("mythicisland")
                        name.set("MythicIsland Team")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/mythicisland/template-gradle.git")
                    developerConnection.set("scm:git:ssh://github.com/mythicisland/template-gradle.git")
                    url.set("https://github.com/mythicisland/template-gradle")
                }
            }
        }
    }

    repositories {
        maven {
            name = "releases"
            url = uri("https://repo.xxjanisxx.dev/releases")
            credentials {
                username = findProperty("repoUser") as String? ?: System.getenv("REPO_USER")
                password = findProperty("repoPassword") as String? ?: System.getenv("REPO_PASSWORD")
            }
        }

        maven {
            name = "snapshots"
            url = uri("https://repo.xxjanisxx.dev/snapshots")
            credentials {
                username = findProperty("repoUser") as String? ?: System.getenv("REPO_USER")
                password = findProperty("repoPassword") as String? ?: System.getenv("REPO_PASSWORD")
            }
        }
    }
}