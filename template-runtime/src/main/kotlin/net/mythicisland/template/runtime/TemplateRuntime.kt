package net.mythicisland.template.runtime

import app.simplecloud.api.CloudApi
import app.simplecloud.api.CloudApiOptions
import net.mythicisland.template.runtime.api.WebServer
import net.mythicisland.template.runtime.database.DatabaseFactory
import net.mythicisland.template.runtime.launcher.TemplateStartCommand
import org.apache.logging.log4j.LogManager

class TemplateRuntime(
    private val args: TemplateStartCommand
) {
    private val logger = LogManager.getLogger(TemplateRuntime::class.java)

    private val database = DatabaseFactory.createDatabase(args.databaseUrl)

    private lateinit var cloudApi: CloudApi

    fun start() {
        logger.info("Starting TemplateRuntime")
        connectToController()
        setupDatabase()
        loadData()
        WebServer.start(args.restHost, args.restPort)
    }

    private fun connectToController() {
        try {
            logger.info("Connecting to your Network...")
            cloudApi = CloudApi.create(
                CloudApiOptions.builder()
                    .networkId(args.networkId)
                    .networkSecret(args.networkSecret)
                    .controllerUrl(args.controllerUrl)
                    .natsUrl(args.controllerNatsUrl)
                    .build()
            )
            logger.info("Successfully connected to your Network")
            logger.info("Network ID: {}", cloudApi.networkId)
        } catch (e: Exception) {
            logger.info("Failed to connect to your Network", e)
            throw e
        }
    }

    private fun setupDatabase() {
        try {
            logger.info("Setting up database...")
            database.setup()
            logger.info("Successfully setup database")
        } catch (e: Exception) {
            logger.error("Failed to setup database", e)
            throw e
        }
    }

    private fun loadData() {
        try {
            logger.info("Loading initial data...")
            logger.info("Successfully loaded initial data")
        } catch (e: Exception) {
            logger.error("Failed to load initial data", e)
            throw e
        }
    }
}