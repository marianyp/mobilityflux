package dev.mariany.mobilityflux.client.render.item.property.bool;

import com.mojang.serialization.MapCodec;
import dev.mariany.mobilityflux.entity.gatecrash.EntityWithGatecrashState;
import net.minecraft.client.render.item.property.bool.BooleanProperty;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public record TetheredProperty() implements BooleanProperty {
    public static final MapCodec<TetheredProperty> CODEC = MapCodec.unit(new TetheredProperty());

    @Override
    public boolean test(
            ItemStack stack,
            @Nullable ClientWorld world,
            @Nullable LivingEntity livingEntity,
            int seed,
            ItemDisplayContext displayContext
    ) {
        if (livingEntity instanceof EntityWithGatecrashState entityWithGatecrashState) {
            return entityWithGatecrashState.mobilityFlux$getGatecrashState().isTethered();
        }

        return false;
    }

    @Override
    public MapCodec<TetheredProperty> getCodec() {
        return CODEC;
    }
}