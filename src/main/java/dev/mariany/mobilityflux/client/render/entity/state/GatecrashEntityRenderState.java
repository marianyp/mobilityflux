package dev.mariany.mobilityflux.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.state.EntityRenderState;

@Environment(EnvType.CLIENT)
public class GatecrashEntityRenderState extends EntityRenderState {
    public float progress;
}
