package net.mythicisland.template.runtime.launcher

import com.github.ajalt.clikt.command.SuspendingCliktCommand
import com.github.ajalt.clikt.core.context
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.int
import com.github.ajalt.clikt.sources.PropertiesValueSource
import com.github.ajalt.clikt.sources.ValueSource
import net.mythicisland.template.runtime.TemplateRuntime
import java.io.File

object TemplateStartCommand : SuspendingCliktCommand() {

    init {
        context {
            valueSource = PropertiesValueSource.from(File("template.properties"), false, ValueSource.envvarKey())
        }
    }

    val databaseUrl: String by option(help = "Database URL", envvar = "DATABASE_URL")
        .default("jdbc:postgresql://localhost:5432/template?user=template&password=super-secret-password")

    val restHost: String by option(help = "REST API URL", envvar = "REST_URL")
        .default("")

    val restPort: Int by option(help = "Rest API Port", envvar = "REST_PORT")
        .int().default(8143)

    val networkId: String by option(help = "Simplecloud Network ID", envvar = "NETWORK_ID")
        .default("your-network-id")

    val networkSecret: String by option(help = "Simplecloud Network Secret", envvar = "NETWORK_SECRET")
        .default("your-super-secret")

    val controllerUrl: String by option(help = "Simplecloud Controller URL", envvar = "CONTROLLER_URL")
        .default("https://controller.platform.simplecloud.app")

    val controllerNatsUrl: String by option(help = "Simplecloud Controller Nats URL", envvar = "CONTROLLER_NATS_URL")
        .default("nats://platform.simplecloud.app:4222")

    override suspend fun run() {
        TemplateRuntime(this).start()
    }
}