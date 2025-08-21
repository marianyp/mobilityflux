package dev.mariany.mobilityflux.datagen;

import dev.mariany.mobilityflux.client.render.item.property.bool.TetheredProperty;
import dev.mariany.mobilityflux.item.MFItems;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.client.data.*;
import net.minecraft.client.render.item.property.bool.BooleanProperty;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

import java.util.function.Supplier;

public class MFModelProvider extends FabricModelProvider {
    public MFModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.register(MFItems.SHOCKWAVE_GRENADE, Models.GENERATED);
        itemModelGenerator.register(MFItems.THERMAL_PEARL, Models.GENERATED);
        itemModelGenerator.register(MFItems.ALLAY, Models.GENERATED);
        registerBooleanModel(itemModelGenerator, MFItems.GATECRASH, TetheredProperty::new, "tethered");
    }

    public final void registerBooleanModel(
            ItemModelGenerator itemModelGenerator,
            Item item,
            Supplier<BooleanProperty> property,
            String suffix
    ) {
        Identifier falseTexture = TextureMap.getId(item);
        Identifier trueTexture = ModelIds.getItemSubModelId(item, "_" + suffix);

        Models.GENERATED.upload(
                falseTexture,
                TextureMap.layer0(falseTexture),
                itemModelGenerator.modelCollector
        );

        Models.GENERATED.upload(
                trueTexture,
                TextureMap.layer0(trueTexture),
                itemModelGenerator.modelCollector
        );

        itemModelGenerator.output
                .accept(
                        item,
                        ItemModels.condition(
                                property.get(),
                                ItemModels.basic(trueTexture),
                                ItemModels.basic(falseTexture)
                        )
                );
    }
}
