plugins {
    application
}

application {
    mainClass.set("net.mythicisland.template.runtime.launcher.LauncherKt")
}

dependencies {
    implementation(project(":template-shared"))
}