package dev.mariany.mobilityflux.datagen;

import dev.mariany.mobilityflux.item.MFItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class MFRecipeProvider extends FabricRecipeProvider {
    public MFRecipeProvider(
            FabricDataOutput output,
            CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture
    ) {
        super(output, registriesFuture);
    }

    @Override
    protected RecipeGenerator getRecipeGenerator(
            RegistryWrapper.WrapperLookup wrapperLookup,
            RecipeExporter recipeExporter
    ) {
        return new RecipeGenerator(wrapperLookup, recipeExporter) {
            @Override
            public void generate() {
                this.createShaped(RecipeCategory.MISC, MFItems.SHOCKWAVE_GRENADE, 4)
                        .pattern(" I ")
                        .pattern("IAI")
                        .pattern(" I ")
                        .input('I', Items.IRON_NUGGET)
                        .input('A', Items.AMETHYST_SHARD)
                        .criterion(hasItem(Items.AMETHYST_SHARD), conditionsFromItem(Items.AMETHYST_SHARD))
                        .offerTo(this.exporter);

                this.createShaped(RecipeCategory.MISC, MFItems.GATECRASH)
                        .pattern("LLL")
                        .pattern("LPL")
                        .pattern("LLL")
                        .input('L', Items.LAPIS_LAZULI)
                        .input('P', Items.ENDER_PEARL)
                        .criterion(hasItem(Items.ENDER_PEARL), conditionsFromItem(Items.ENDER_PEARL))
                        .offerTo(this.exporter);

                this.createShapeless(RecipeCategory.MISC, MFItems.THERMAL_PEARL)
                        .input(Items.HEART_OF_THE_SEA)
                        .input(Items.LAVA_BUCKET)
                        .criterion(hasItem(Items.HEART_OF_THE_SEA), conditionsFromItem(Items.HEART_OF_THE_SEA))
                        .offerTo(this.exporter);
            }
        };
    }

    @Override
    public String getName() {
        return "Mobility Flux Recipes";
    }
}
