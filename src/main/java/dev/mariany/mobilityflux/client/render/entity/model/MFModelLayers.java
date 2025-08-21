package dev.mariany.mobilityflux.client.render.entity.model;

import dev.mariany.mobilityflux.MobilityFlux;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.render.entity.model.EntityModelLayer;

@Environment(EnvType.CLIENT)
public class MFModelLayers {
    public static final EntityModelLayer GATECRASH = create("gatecrash");

    private static EntityModelLayer create(String id) {
        return new EntityModelLayer(MobilityFlux.id(id), "main");
    }

    public static void bootstrap() {
        MobilityFlux.LOGGER.info("Registering Model Layers for " + MobilityFlux.MOD_ID);

        EntityModelLayerRegistry.registerModelLayer(GATECRASH, GatecrashEntityModel::getTexturedModelData);
    }
}
