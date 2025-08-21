package dev.mariany.mobilityflux.packet.clientbound;

import dev.mariany.mobilityflux.MobilityFlux;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record StartAllayImmunity(int id) implements CustomPayload {
    public static final Id<StartAllayImmunity> ID = new Id<>(
            MobilityFlux.id("start_allay_immunity"));
    public static final PacketCodec<RegistryByteBuf, StartAllayImmunity> CODEC =
            PacketCodec.tuple(PacketCodecs.VAR_INT, StartAllayImmunity::id, StartAllayImmunity::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
