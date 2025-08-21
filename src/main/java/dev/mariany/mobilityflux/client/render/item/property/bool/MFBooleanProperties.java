package dev.mariany.mobilityflux.client.render.item.property.bool;

import com.mojang.serialization.MapCodec;
import dev.mariany.mobilityflux.MobilityFlux;
import net.minecraft.client.render.item.property.bool.BooleanProperties;
import net.minecraft.client.render.item.property.bool.BooleanProperty;

public class MFBooleanProperties {
    public static void bootstrap() {
        MobilityFlux.LOGGER.info("Registering Boolean Properties for " + MobilityFlux.MOD_ID);

        register("tethered", TetheredProperty.CODEC);
    }

    private static void register(String name, MapCodec<? extends BooleanProperty> codec) {
        BooleanProperties.ID_MAPPER.put(MobilityFlux.id(name), codec);
    }
}
