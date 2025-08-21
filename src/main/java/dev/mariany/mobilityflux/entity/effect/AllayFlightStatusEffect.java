package dev.mariany.mobilityflux.entity.effect;

import dev.mariany.mobilityflux.MFUtil;
import dev.mariany.mobilityflux.entity.allay.EntityWithAllay;
import dev.mariany.mobilityflux.packet.clientbound.StartAllayImmunity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

public class AllayFlightStatusEffect extends StatusEffect implements ExtendedStatusEffect {
    private static final float DEFAULT_FLY_SPEED = 0.05F;
    private static final float FLY_SPEED = 0.0125F;

    protected AllayFlightStatusEffect(StatusEffectCategory category, int color) {
        super(category, color, ParticleTypes.GLOW);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    @Override
    public boolean applyUpdateEffect(ServerWorld world, LivingEntity entity, int amplifier) {
        if (entity instanceof ServerPlayerEntity player) {
            PlayerAbilities abilities = player.getAbilities();

            if (abilities.getFlySpeed() != FLY_SPEED || !abilities.allowFlying) {
                abilities.setFlySpeed(FLY_SPEED);
                abilities.allowFlying = true;
                player.sendAbilitiesUpdate();
            }
        }

        return true;
    }

    @Override
    public void mobilityFlux$onRemoved(LivingEntity entity, boolean upgrade) {
        if (!upgrade) {
            if (entity instanceof ServerPlayerEntity player) {
                resetAbilities(player);

                player.playSoundToPlayer(
                        SoundEvents.ENTITY_ALLAY_AMBIENT_WITHOUT_ITEM,
                        SoundCategory.NEUTRAL,
                        0.25F,
                        1
                );

                if (entity instanceof EntityWithAllay entityWithAllay) {
                    entityWithAllay.mobilityFlux$dismount(true);
                    entityWithAllay.mobilityFlux$getAllayFallState().makeFallDamageImmune();
                    MFUtil.sendToEntityAndTrackers(player, new StartAllayImmunity(player.getId()));
                }
            }
        }
    }

    private static void resetAbilities(ServerPlayerEntity player) {
        PlayerAbilities abilities = player.getAbilities();
        abilities.setFlySpeed(DEFAULT_FLY_SPEED);
        player.getGameMode().setAbilities(abilities);
        player.sendAbilitiesUpdate();
    }
}
