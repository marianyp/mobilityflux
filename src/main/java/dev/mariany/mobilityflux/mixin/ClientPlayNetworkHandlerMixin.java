package dev.mariany.mobilityflux.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.mariany.mobilityflux.sound.MFSoundEvents;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    @WrapOperation(
            method = "onExplosion",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/world/ClientWorld;playSoundClient(DDDLnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FFZ)V"
            )
    )
    public void wrapOnExplosion(
            ClientWorld instance,
            double x,
            double y,
            double z,
            SoundEvent sound,
            SoundCategory category,
            float volume,
            float pitch,
            boolean useDistance,
            Operation<Void> original
    ) {
        if (MFSoundEvents.ENTITY_SHOCKWAVE_GRENADE_EXPLODE.matchesId(sound.id())) {
            float newPitch = MathHelper.nextBetween(instance.random, 0.9F, 1.1F);
            original.call(instance, x, y, z, sound, category, volume, newPitch, useDistance);
        }
    }
}
