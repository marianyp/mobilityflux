package dev.mariany.mobilityflux.mixin;

import dev.mariany.mobilityflux.entity.shockwave.EntityWithShockwaveState;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {
    @Shadow
    public ServerPlayerEntity player;

    @Inject(method = "shouldCheckMovement", at = @At(value = "HEAD"), cancellable = true)
    private void injectShouldCheckMovement(boolean elytra, CallbackInfoReturnable<Boolean> cir) {
        if (this.player instanceof EntityWithShockwaveState entityWithShockwaveState) {
            if (entityWithShockwaveState.mobilityFlux$getShockwaveState().hasLowGravity()) {
                cir.setReturnValue(false);
            }
        }
    }
}
