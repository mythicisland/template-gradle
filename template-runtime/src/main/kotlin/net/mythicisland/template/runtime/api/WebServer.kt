package net.mythicisland.template.runtime.api

import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import org.apache.logging.log4j.LogManager

object WebServer {
    private lateinit var server: EmbeddedServer<*, *>

    private val logger = LogManager.getLogger(WebServer::class.java)

    fun start(host: String, port: Int) {
        server = embeddedServer(Netty, host = host, port = port, module = Application::module)
        logger.info("Starting Rest Server on $host:$port...")
        server.start(wait = true)
    }

    fun stop() = server.stop()

}

fun Application.module() {
    configureCors()
    configureContentNegotiation()
    configureRouting()
}

fun Application.configureCors() {
    install(CORS) {
        anyHost()
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Patch)
        allowHeader(HttpHeaders.AccessControlAllowOrigin)
        allowHeader(HttpHeaders.ContentType)
    }
}

fun Application.configureContentNegotiation() {
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
            explicitNulls = false
        })
    }
}

fun Application.configureRouting() {
    routing {
        route("/api/v1") {
        }
    }
}