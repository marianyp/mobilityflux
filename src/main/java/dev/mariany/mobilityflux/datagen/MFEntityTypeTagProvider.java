package dev.mariany.mobilityflux.datagen;

import dev.mariany.mobilityflux.entity.MFEntities;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.EntityTypeTags;

import java.util.concurrent.CompletableFuture;

public class MFEntityTypeTagProvider extends FabricTagProvider.EntityTypeTagProvider {
    public MFEntityTypeTagProvider(
            FabricDataOutput output,
            CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture
    ) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        this.valueLookupBuilder(EntityTypeTags.IMPACT_PROJECTILES).add(MFEntities.SHOCKWAVE_GRENADE);
    }
}
