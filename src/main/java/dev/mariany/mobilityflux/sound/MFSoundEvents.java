package dev.mariany.mobilityflux.sound;

import dev.mariany.mobilityflux.MobilityFlux;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class MFSoundEvents {
    public static final SoundEvent ITEM_SHOCKWAVE_GRENADE_THROW = register("item.shockwave_grenade.throw");
    public static final SoundEvent ENTITY_SHOCKWAVE_GRENADE_PRIME = register("entity.shockwave_grenade.prime");
    public static final RegistryEntry<SoundEvent> ENTITY_SHOCKWAVE_GRENADE_EXPLODE = registerReference(
            "entity.shockwave_grenade.explode");

    public static final SoundEvent ITEM_GATECRASH_RELEASE = register("item.gatecrash.release");
    public static final SoundEvent ITEM_GATECRASH_RETURN = register("item.gatecrash.return");
    public static final SoundEvent ITEM_GATECRASH_DESTROY = register("item.gatecrash.destroy");
    public static final SoundEvent ITEM_THERMAL_PEARL_USE = register("item.thermal_pearl.use");

    private static SoundEvent register(String id) {
        return register(MobilityFlux.id(id));
    }

    private static SoundEvent register(Identifier id) {
        return register(id, id);
    }

    private static RegistryEntry.Reference<SoundEvent> registerReference(String id) {
        return registerReference(MobilityFlux.id(id));
    }

    private static RegistryEntry.Reference<SoundEvent> registerReference(Identifier id) {
        return registerReference(id, id);
    }

    private static RegistryEntry.Reference<SoundEvent> registerReference(Identifier id, Identifier soundId) {
        return Registry.registerReference(Registries.SOUND_EVENT, id, SoundEvent.of(soundId));
    }

    private static SoundEvent register(Identifier id, Identifier soundId) {
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(soundId));
    }

    public static void bootstrap() {
        MobilityFlux.LOGGER.info("Registering Sound Events for " + MobilityFlux.MOD_ID);
    }
}
