package dev.mariany.mobilityflux.client;

import dev.mariany.mobilityflux.client.render.entity.GatecrashEntityRenderer;
import dev.mariany.mobilityflux.client.render.entity.feature.MountedAllayFeatureRenderer;
import dev.mariany.mobilityflux.client.render.entity.model.MFModelLayers;
import dev.mariany.mobilityflux.client.render.item.property.bool.MFBooleanProperties;
import dev.mariany.mobilityflux.entity.MFEntities;
import dev.mariany.mobilityflux.packet.clientbound.ClientboundPackets;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;

public class MobilityFluxClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientboundPackets.init();
        MFBooleanProperties.bootstrap();
        MFModelLayers.bootstrap();
        registerEntityRenderer();

        LivingEntityFeatureRendererRegistrationCallback.EVENT.register(this::registerEntityFeatures);
    }

    private void registerEntityFeatures(
            EntityType<? extends LivingEntity> entityType,
            LivingEntityRenderer<?, ?, ?> livingEntityRenderer,
            LivingEntityFeatureRendererRegistrationCallback.RegistrationHelper registrationHelper,
            EntityRendererFactory.Context context
    ) {
        if (livingEntityRenderer instanceof PlayerEntityRenderer playerEntityRenderer) {
            registrationHelper.register(
                    new MountedAllayFeatureRenderer(playerEntityRenderer, context.getEntityModels())
            );
        }
    }

    private static void registerEntityRenderer() {
        EntityRendererRegistry.register(MFEntities.SHOCKWAVE_GRENADE, FlyingItemEntityRenderer::new);
        EntityRendererRegistry.register(MFEntities.GATECRASH, GatecrashEntityRenderer::new);
    }
}
