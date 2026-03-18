package net.mythicisland.template.runtime

import app.simplecloud.api.CloudApi
import app.simplecloud.api.CloudApiOptions
import io.grpc.Server
import io.grpc.ServerBuilder
import io.nats.client.Connection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import net.mythicisland.template.runtime.database.DatabaseFactory
import net.mythicisland.template.runtime.launcher.TemplateStartCommand
import net.mythicisland.template.runtime.nats.NatsConnectionHandler
import net.mythicisland.template.runtime.nats.NatsErrorListener
import net.mythicisland.template.runtime.nats.NatsFailoverConnectionManager
import org.apache.logging.log4j.LogManager

class TemplateRuntime(
    private val args: TemplateStartCommand
) {
    private val logger = LogManager.getLogger(TemplateRuntime::class.java)

    private val api = connectToController()

    private val natsConnectionHandler = NatsConnectionHandler()
    private val natsErrorListener = NatsErrorListener()
    private val manager = createNatsConnectionManager()

    private var natsConnection: Connection? = null

    private val database = DatabaseFactory.createDatabase(args.databaseUrl)

    suspend fun start() {
        logger.info("Starting Template Droplet...")

        connectNats()

        database.setup()

        val server = createGrpcServer()
        startGrpcServer(server)

        logger.info("Template Droplet started successfully")

        suspendCancellableCoroutine { continuation ->
            Runtime.getRuntime().addShutdownHook(Thread {
                logger.info("Shutting down Template Droplet...")
                server.shutdown()
                continuation.resume(Unit) { cause, _, _ ->
                    logger.info("Runtime shutdown due to: $cause")
                }
            })
        }
    }

    private fun connectToController(): CloudApi {
        try {
            logger.info("Connecting to your Network...")
            val api = CloudApi.create(
                CloudApiOptions.builder()
                    .networkId(args.networkId)
                    .networkSecret(args.networkSecret)
                    .controllerUrl(args.controllerUrl)
                    .natsUrl(args.controllerNatsUrl)
                    .build()
            )
            logger.info("Successfully connected to your Network")
            logger.info("Network ID: {}", api.networkId)
            return api
        } catch (e: Exception) {
            logger.error("Failed to connect to your Network", e)
            throw e
        }
    }

    private fun connectNats() {
        try {
            logger.info("Connecting to NATS...")
            logger.info("NATS failover full reconnect timeout: {}", args.natsFailoverReconnectAfter)

            natsConnection = manager.connection()
            logger.info("Successfully connected to NATS")
        } catch (e: Exception) {
            logger.error("Failed to connect to NATS", e)
            throw e
        }
    }

    private fun startGrpcServer(server: Server) {
        logger.info("Starting gRPC server on port {}...", args.grpcPort)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                server.start()
                logger.info("gRPC server started on port {}", args.grpcPort)
                server.awaitTermination()
            } catch (e: Exception) {
                logger.error("Error in gRPC server", e)
                throw e
            }
        }
    }

    private fun createGrpcServer(): Server {
        return ServerBuilder.forPort(args.grpcPort)
            .build()
    }

    private fun createNatsConnectionManager(): NatsFailoverConnectionManager {
        return NatsFailoverConnectionManager(
            natsUrl = args.natsUrl,
            natsUser = args.natsUser,
            natsSecret = args.natsSecret,
            errorListener = natsErrorListener,
            connectionHandler = natsConnectionHandler,
            failoverReconnectAfter = args.natsFailoverReconnectAfter,
        )
    }
}