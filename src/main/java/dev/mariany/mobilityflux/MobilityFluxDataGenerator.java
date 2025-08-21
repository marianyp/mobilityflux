package dev.mariany.mobilityflux;

import dev.mariany.mobilityflux.datagen.MFModelProvider;
import dev.mariany.mobilityflux.datagen.MFRecipeProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class MobilityFluxDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(MFModelProvider::new);
        pack.addProvider(MFRecipeProvider::new);
    }
}
