package dev.mariany.mobilityflux.client.render.entity.model;

import dev.mariany.mobilityflux.client.render.entity.state.GatecrashEntityRenderState;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.EntityModel;

@Environment(EnvType.CLIENT)
public class GatecrashEntityModel extends EntityModel<GatecrashEntityRenderState> {
    private static final String ORB_NAME = "orb";
    private static final String SHADOW_NAME = "shadow";

    protected final ModelPart orb;

    public GatecrashEntityModel(ModelPart root) {
        super(root);
        this.orb = root.getChild(ORB_NAME);
    }

    public static TexturedModelData getTexturedModelData() {
        return getTexturedModelData(Dilation.NONE);
    }

    public static TexturedModelData getTexturedModelData(Dilation dilation) {
        return TexturedModelData.of(getModelData(dilation), 32, 32);
    }

    protected static ModelData getModelData(Dilation dilation) {
        ModelData modelData = new ModelData();
        ModelPartData root = modelData.getRoot();

        root.addChild(ORB_NAME,
                ModelPartBuilder.create()
                        .uv(0, 0)
                        .cuboid(
                                -2.5F,
                                -10F,
                                -2.5F,
                                5F,
                                5F,
                                5F,
                                dilation
                        ),
                ModelTransform.origin(0F, 16F, 0F)
        );

        root.addChild(SHADOW_NAME,
                ModelPartBuilder.create()
                        .uv(0, 10)
                        .cuboid(-1.5F, 0F, -1.5F, 3F, 0.001F, 3F, dilation),
                ModelTransform.origin(0F, 0.01F, 0F)
        );

        return modelData;
    }

    public void setYaw(float yawRad) {
        this.orb.yaw = yawRad;
    }
}
