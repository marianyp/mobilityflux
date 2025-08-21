package dev.mariany.mobilityflux.mixin;

import dev.mariany.mobilityflux.client.render.entity.state.EntityWithAllayRenderState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(PlayerEntityRenderState.class)
public class PlayerEntityRenderStateMixin implements EntityWithAllayRenderState {
    @Unique
    private boolean hasAllay;

    @Override
    public boolean mobilityFlux$hasAllay() {
        return this.hasAllay;
    }

    @Override
    public void mobilityFlux$setHasAllay(boolean hasAllay) {
        this.hasAllay = hasAllay;
    }
}
