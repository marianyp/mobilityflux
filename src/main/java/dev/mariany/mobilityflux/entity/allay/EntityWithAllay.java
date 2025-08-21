package dev.mariany.mobilityflux.entity.allay;

import com.mojang.serialization.Codec;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AllayEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;

public interface EntityWithAllay {
    String ALLAY_KEY = "Allay";
    String ALLAY_RETURN_SLOT_KEY = "AllayReturnSlot";

    AllayFallState mobilityFlux$getAllayFallState();

    NbtCompound mobilityFlux$getAllay();

    void mobilityFlux$setAllay(NbtCompound allay);

    boolean mobilityFlux$mount(AllayEntity allay);

    boolean mobilityFlux$mount(AllayEntity allay, int slot);

    void mobilityFlux$dismount(boolean cooldown);

    int mobilityFlux$getReturnSlot();

    void mobilityFlux$setReturnSlot(int slot);

    static void readCustomData(ReadView view, Entity entity) {
        if (!entity.getWorld().isClient() && entity instanceof EntityWithAllay entityWithAllay) {
            entityWithAllay.mobilityFlux$setAllay(
                    view.read(ALLAY_KEY, NbtCompound.CODEC).orElseGet(NbtCompound::new)
            );

            entityWithAllay.mobilityFlux$setReturnSlot(view.read(ALLAY_RETURN_SLOT_KEY, Codec.INT).orElse(-1));
        }
    }

    static void writeCustomData(WriteView view, Entity entity) {
        if (!entity.getWorld().isClient() && entity instanceof EntityWithAllay entityWithAllay) {
            NbtCompound allay = entityWithAllay.mobilityFlux$getAllay();
            int returnSlot = entityWithAllay.mobilityFlux$getReturnSlot();

            if (!allay.isEmpty()) {
                view.put(ALLAY_KEY, NbtCompound.CODEC, allay);
            }

            if (returnSlot > -1) {
                view.putInt(ALLAY_RETURN_SLOT_KEY, returnSlot);
            }
        }
    }
}
