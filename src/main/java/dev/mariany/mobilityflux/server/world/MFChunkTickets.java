package dev.mariany.mobilityflux.server.world;

import dev.mariany.mobilityflux.MobilityFlux;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.world.ChunkTicketType;

public class MFChunkTickets {
    public static final ChunkTicketType GATECRASH =
            register(
                    "gatecrash",
                    40L,
                    false,
                    ChunkTicketType.Use.LOADING_AND_SIMULATION
            );

    private static ChunkTicketType register(String id, long expiryTicks, boolean persist, ChunkTicketType.Use use) {
        return Registry.register(Registries.TICKET_TYPE, id, new ChunkTicketType(expiryTicks, persist, use));
    }

    public static void bootstrap() {
        MobilityFlux.LOGGER.info("Registering Chunk Tickets for {}", MobilityFlux.MOD_ID);
    }
}
