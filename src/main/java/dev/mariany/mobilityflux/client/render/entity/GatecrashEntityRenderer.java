package dev.mariany.mobilityflux.client.render.entity;

import dev.mariany.mobilityflux.MobilityFlux;
import dev.mariany.mobilityflux.client.render.entity.model.GatecrashEntityModel;
import dev.mariany.mobilityflux.client.render.entity.model.MFModelLayers;
import dev.mariany.mobilityflux.client.render.entity.state.GatecrashEntityRenderState;
import dev.mariany.mobilityflux.entity.gatecrash.GatecrashEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class GatecrashEntityRenderer extends EntityRenderer<GatecrashEntity, GatecrashEntityRenderState> {
    private static final Identifier TEXTURE = MobilityFlux.id("textures/entity/gatecrash.png");
    private final GatecrashEntityModel model;

    public GatecrashEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.model = new GatecrashEntityModel(context.getPart(MFModelLayers.GATECRASH));
    }

    @Override
    public GatecrashEntityRenderState createRenderState() {
        return new GatecrashEntityRenderState();
    }

    @Override
    public void updateRenderState(GatecrashEntity entity, GatecrashEntityRenderState state, float tickProgress) {
        super.updateRenderState(entity, state, tickProgress);
        state.progress = entity.age + tickProgress;
    }

    @Override
    public void render(
            GatecrashEntityRenderState state,
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            int light
    ) {
        matrices.push();
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(this.model.getLayer(TEXTURE));
        this.model.setYaw((state.progress * 16F) * ((float) Math.PI / 180F));
        this.model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
        matrices.pop();
        super.render(state, matrices, vertexConsumers, light);
    }
}
