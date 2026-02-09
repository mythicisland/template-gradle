import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.shadow)
    alias(libs.plugins.jooq.codegen)
    application
}

application {
    mainClass.set("net.mythicisland.template.runtime.launcher.LauncherKt")
}

dependencies {
    implementation(project(":template-shared"))
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.bundles.logging)
    implementation(libs.clikt)
    implementation(libs.caffeine)
    implementation(libs.jnats)
    implementation(libs.postgre.jdbc)
    implementation(libs.bundles.jooq)
    implementation(libs.bundles.ktor.server)
    implementation(libs.bundles.grpc)
}

tasks.named<ShadowJar>("shadowJar") {
    mergeServiceFiles()
    archiveClassifier.set("")
}

sourceSets {
    main {
        java {
            srcDirs(
                "build/generated/source/db/main/java",
            )
        }
        resources {
            srcDirs(
                "src/main/db"
            )
        }
    }
}

tasks.named("compileKotlin") {
    dependsOn(tasks.jooqCodegen)
}

jooq {
    configuration {
        generator {
            name = "org.jooq.codegen.KotlinGenerator"
            target {
                directory = "build/generated/source/db/main/java"
                packageName = "net.mythicisland.template.db"
            }
            generate {
                implicitJoinPathsAsKotlinProperties = false
                implicitJoinPathTableSubtypes = false
                implicitJoinPathsToMany = false
                implicitJoinPathsToOne = false
            }
            database {
                name = "org.jooq.meta.extensions.ddl.DDLDatabase"
                properties {
                    property {
                        key = "scripts"
                        value = "src/main/db/schema.sql"
                    }
                    property {
                        key = "sort"
                        value = "semantic"
                    }
                    property {
                        key = "unqualifiedSchema"
                        value = "none"
                    }
                    property {
                        key = "defaultNameCase"
                        value = "lower"
                    }
                }
            }
        }
    }
}