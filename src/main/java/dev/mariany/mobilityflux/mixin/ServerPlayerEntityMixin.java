package dev.mariany.mobilityflux.mixin;

import dev.mariany.mobilityflux.entity.allay.EntityWithAllay;
import dev.mariany.mobilityflux.entity.shockwave.EntityWithShockwaveState;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {
    @Inject(method = "tick", at = @At(value = "HEAD"))
    public void injectTick(CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;

        if (this instanceof EntityWithShockwaveState entityWithShockwaveState) {
            entityWithShockwaveState.mobilityFlux$getShockwaveState().tick(player);
        }

        if (this instanceof EntityWithAllay entityWithAllay) {
            entityWithAllay.mobilityFlux$getAllayFallState().tick(player);
        }
    }
}
