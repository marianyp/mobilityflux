package dev.mariany.mobilityflux.client.render.entity.feature;

import dev.mariany.mobilityflux.client.render.entity.state.EntityWithAllayRenderState;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.AllayEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.AllayEntityRenderState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class MountedAllayFeatureRenderer extends FeatureRenderer<PlayerEntityRenderState, PlayerEntityModel> {
    private static final Identifier ALLAY_TEXTURE = Identifier.ofVanilla("textures/entity/allay/allay.png");

    private final AllayEntityModel model;
    private final AllayEntityRenderState allayState = new AllayEntityRenderState();

    public MountedAllayFeatureRenderer(
            FeatureRendererContext<PlayerEntityRenderState, PlayerEntityModel> context,
            LoadedEntityModels loader
    ) {
        super(context);
        this.model = new AllayEntityModel(loader.getModelPart(EntityModelLayers.ALLAY));
    }

    @Override
    public void render(
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            int light,
            PlayerEntityRenderState state,
            float limbAngle,
            float limbDistance
    ) {
        if (state instanceof EntityWithAllayRenderState entityWithAllayRenderState) {
            if (entityWithAllayRenderState.mobilityFlux$hasAllay()) {
                matrices.push();
                matrices.translate(0, state.isInSneakingPose ? -1.3 : -1.5, 0.425);

                this.allayState.age = state.age;
                this.allayState.limbSwingAnimationProgress = state.limbSwingAnimationProgress * 0.5F;
                this.allayState.relativeHeadYaw = limbAngle;
                this.allayState.pitch = limbDistance;
                this.allayState.itemHoldAnimationTicks = 1;

                this.model.setAngles(this.allayState);
                this.model.render(
                        matrices,
                        vertexConsumers.getBuffer(this.model.getLayer(ALLAY_TEXTURE)),
                        LightmapTextureManager.MAX_LIGHT_COORDINATE,
                        OverlayTexture.DEFAULT_UV
                );

                matrices.pop();
            }
        }
    }
}
