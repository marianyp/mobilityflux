package dev.mariany.mobilityflux;

import dev.mariany.mobilityflux.entity.MFEntities;
import dev.mariany.mobilityflux.entity.effect.MFStatusEffects;
import dev.mariany.mobilityflux.event.entity.UseEntityHandler;
import dev.mariany.mobilityflux.item.MFItems;
import dev.mariany.mobilityflux.packet.MFPackets;
import dev.mariany.mobilityflux.server.world.MFChunkTickets;
import dev.mariany.mobilityflux.sound.MFSoundEvents;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MobilityFlux implements ModInitializer {
    public static final String MOD_ID = "mobilityflux";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static Identifier id(String resource) {
        return Identifier.of(MOD_ID, resource);
    }

    @Override
    public void onInitialize() {
        MFPackets.register();
        MFChunkTickets.bootstrap();
        MFSoundEvents.bootstrap();
        MFStatusEffects.bootstrap();
        MFItems.bootstrap();
        MFEntities.bootstrap();

        UseEntityCallback.EVENT.register(UseEntityHandler::onUseEntity);
    }
}