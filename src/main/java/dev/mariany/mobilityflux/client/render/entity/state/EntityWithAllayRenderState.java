package dev.mariany.mobilityflux.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface EntityWithAllayRenderState {
    boolean mobilityFlux$hasAllay();
    void mobilityFlux$setHasAllay(boolean hasAllay);
}
