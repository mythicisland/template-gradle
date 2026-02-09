plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "template-gradle"

dependencyResolutionManagement {
    versionCatalogs {
        create("libs")
    }
}

include(
    "template-api",
    "template-runtime",
    "template-shared"
)