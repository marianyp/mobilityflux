package dev.mariany.mobilityflux.packet;

import dev.mariany.mobilityflux.packet.clientbound.StartAllayImmunity;
import dev.mariany.mobilityflux.packet.clientbound.StartShockwaveImmunity;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryByteBuf;

public class MFPackets {
    public static void register() {
        clientbound(PayloadTypeRegistry.playS2C());
        serverbound(PayloadTypeRegistry.playC2S());
    }

    private static void clientbound(PayloadTypeRegistry<RegistryByteBuf> registry) {
        registry.register(StartShockwaveImmunity.ID, StartShockwaveImmunity.CODEC);
        registry.register(StartAllayImmunity.ID, StartAllayImmunity.CODEC);
    }

    private static void serverbound(PayloadTypeRegistry<RegistryByteBuf> registry) {
    }
}
