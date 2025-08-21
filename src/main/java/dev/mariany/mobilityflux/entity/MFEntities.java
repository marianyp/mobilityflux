package dev.mariany.mobilityflux.entity;

import dev.mariany.mobilityflux.MobilityFlux;
import dev.mariany.mobilityflux.entity.gatecrash.GatecrashEntity;
import dev.mariany.mobilityflux.entity.shockwave.ShockwaveGrenadeEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

public class MFEntities {
    public static final EntityType<ShockwaveGrenadeEntity> SHOCKWAVE_GRENADE = register(
            "shockwave_grenade",
            EntityType.Builder.<ShockwaveGrenadeEntity>create(ShockwaveGrenadeEntity::new, SpawnGroup.MISC)
                    .dropsNothing()
                    .dimensions(0.25F, 0.25F)
                    .maxTrackingRange(4)
                    .trackingTickInterval(10)
    );

    public static final EntityType<GatecrashEntity> GATECRASH = register(
            "gatecrash",
            EntityType.Builder.create(GatecrashEntity::new, SpawnGroup.MISC)
                    .dropsNothing()
                    .dimensions(0.4F, 0.7F)
                    .maxTrackingRange(4)
                    .trackingTickInterval(10)
    );

    private static <T extends Entity> EntityType<T> register(
            RegistryKey<EntityType<?>> key,
            EntityType.Builder<T> type
    ) {
        return Registry.register(Registries.ENTITY_TYPE, key, type.build(key));
    }

    private static RegistryKey<EntityType<?>> keyOf(String id) {
        return RegistryKey.of(RegistryKeys.ENTITY_TYPE, MobilityFlux.id(id));
    }

    private static <T extends Entity> EntityType<T> register(String id, EntityType.Builder<T> type) {
        return register(keyOf(id), type);
    }

    public static void bootstrap() {
        MobilityFlux.LOGGER.info("Registering Entities for " + MobilityFlux.MOD_ID);
    }
}
