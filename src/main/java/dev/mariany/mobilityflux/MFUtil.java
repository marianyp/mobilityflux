package dev.mariany.mobilityflux;

import dev.mariany.mobilityflux.entity.allay.EntityWithAllay;
import dev.mariany.mobilityflux.entity.shockwave.EntityWithShockwaveState;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;

public class MFUtil {
    public static boolean hasFallDamageImmunity(Entity entity) {
        if (entity instanceof EntityWithShockwaveState entityWithShockwaveState) {
            if (entityWithShockwaveState.mobilityFlux$getShockwaveState().hasLowGravity()) {
                return true;
            }
        }

        if (entity instanceof EntityWithAllay entityWithAllay) {
            return entityWithAllay.mobilityFlux$getAllayFallState().hasFallDamageImmunity();
        }

        return false;
    }

    public static void sendToEntityAndTrackers(Entity entity, CustomPayload packet) {
        if (entity instanceof ServerPlayerEntity serverPlayer) {
            ServerPlayNetworking.send(serverPlayer, packet);
        }

        for (ServerPlayerEntity player : PlayerLookup.tracking(entity)) {
            if (!player.equals(entity)) {
                ServerPlayNetworking.send(player, packet);
            }
        }
    }
}
