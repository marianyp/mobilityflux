package dev.mariany.mobilityflux.entity.effect;

import dev.mariany.mobilityflux.MobilityFlux;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Colors;

public class MFStatusEffects {
    public static final RegistryEntry<StatusEffect> ALLAY_FLIGHT = register(
            "allay_flight",
            new AllayFlightStatusEffect(StatusEffectCategory.BENEFICIAL, Colors.CYAN)
                    .applySound(SoundEvents.ENTITY_ALLAY_ITEM_GIVEN)
    );

    private static RegistryEntry<StatusEffect> register(String name, StatusEffect statusEffect) {
        return Registry.registerReference(Registries.STATUS_EFFECT, MobilityFlux.id(name), statusEffect);
    }

    public static void bootstrap() {
        MobilityFlux.LOGGER.info("Registering Status Effects for {}", MobilityFlux.MOD_ID);
    }
}
