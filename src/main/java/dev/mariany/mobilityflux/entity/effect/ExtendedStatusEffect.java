package dev.mariany.mobilityflux.entity.effect;

import net.minecraft.entity.LivingEntity;

public interface ExtendedStatusEffect {
    void mobilityFlux$onRemoved(LivingEntity entity, boolean upgrade);
}
