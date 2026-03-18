package net.mythicisland.template.api.internal;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import net.mythicisland.template.api.TemplateApi;
import net.mythicisland.template.api.TemplateApiOptions;
import net.mythicisland.template.api.internal.nats.NatsFailoverConnectionManager;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TemplateApiImpl implements TemplateApi {
    private static final Logger LOGGER = Logger.getLogger(TemplateApiImpl.class.getName());

    private final ManagedChannel grpcChannel;
    private final NatsFailoverConnectionManager natsManager;

    public TemplateApiImpl(TemplateApiOptions options) {
        this.grpcChannel = ManagedChannelBuilder
                .forAddress(options.getGrpcHost(), options.getGrpcPort())
                .usePlaintext()
                .build();

        NatsFailoverConnectionManager nats;
        try {
            nats = new NatsFailoverConnectionManager(
                    options.getNatsUrl(),
                    options.getNatsUser(),
                    options.getNatsSecret(),
                    options.getNatsFailoverReconnectAfter()
            );
        } catch (IOException | InterruptedException e) {
            grpcChannel.shutdownNow();
            throw new RuntimeException("Failed to establish NATS connection", e);
        }
        this.natsManager = nats;
    }

    @Override
    public void close() {
        try {
            natsManager.shutdown();
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to shutdown NATS connection manager", e);
        }

        try {
            grpcChannel.shutdown();
            if (!grpcChannel.awaitTermination(5, TimeUnit.SECONDS)) {
                grpcChannel.shutdownNow();
            }
        } catch (InterruptedException e) {
            grpcChannel.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

}
