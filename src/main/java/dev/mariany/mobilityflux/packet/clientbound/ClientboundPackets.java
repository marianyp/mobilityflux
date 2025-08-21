package dev.mariany.mobilityflux.packet.clientbound;

import dev.mariany.mobilityflux.entity.allay.EntityWithAllay;
import dev.mariany.mobilityflux.entity.shockwave.EntityWithShockwaveState;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;

public class ClientboundPackets {
    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(
                StartShockwaveImmunity.ID,
                (payload, context) -> {
                    ClientWorld world = context.client().world;

                    if (world != null) {
                        Entity entity = world.getEntityById(payload.id());

                        if (entity instanceof EntityWithShockwaveState entityWithShockwaveState) {
                            entityWithShockwaveState.mobilityFlux$getShockwaveState().startImmunity();
                        }
                    }
                }
        );

        ClientPlayNetworking.registerGlobalReceiver(
                StartAllayImmunity.ID,
                (payload, context) -> {
                    ClientWorld world = context.client().world;

                    if (world != null) {
                        Entity entity = world.getEntityById(payload.id());

                        if (entity instanceof EntityWithAllay entityWithAllay) {
                            entityWithAllay.mobilityFlux$getAllayFallState().makeFallDamageImmune();
                        }
                    }
                }
        );
    }
}
