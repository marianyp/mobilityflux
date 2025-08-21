package dev.mariany.mobilityflux.packet.clientbound;

import dev.mariany.mobilityflux.MobilityFlux;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record StartShockwaveImmunity(int id) implements CustomPayload {
    public static final Id<StartShockwaveImmunity> ID = new Id<>(
            MobilityFlux.id("start_shockwave_immunity"));
    public static final PacketCodec<RegistryByteBuf, StartShockwaveImmunity> CODEC =
            PacketCodec.tuple(PacketCodecs.VAR_INT, StartShockwaveImmunity::id, StartShockwaveImmunity::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
