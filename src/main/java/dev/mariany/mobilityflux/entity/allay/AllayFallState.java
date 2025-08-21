package dev.mariany.mobilityflux.entity.allay;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class AllayFallState {
    private static final String FALL_DAMAGE_IMMUNITY_KEY = "FallDamageImmunity";
    private static final String HAS_TOUCHED_GROUND_KEY = "HasTouchedGround";

    public static final Codec<AllayFallState> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            Codec.BOOL.optionalFieldOf(FALL_DAMAGE_IMMUNITY_KEY, false)
                                    .forGetter(AllayFallState::hasFallDamageImmunity),
                            Codec.BOOL.optionalFieldOf(HAS_TOUCHED_GROUND_KEY, false)
                                    .forGetter(AllayFallState::hasTouchedGround)
                    )
                    .apply(instance, AllayFallState::new)
    );

    private boolean fallDamageImmune;
    private boolean hasTouchedGround;

    public AllayFallState() {
        this(false, false);
    }

    public AllayFallState(boolean fallDamageImmune, boolean hasTouchedGround) {
        this.fallDamageImmune = fallDamageImmune;
        this.hasTouchedGround = hasTouchedGround;
    }

    public void applyState(AllayFallState allayFallState) {
        this.fallDamageImmune = allayFallState.fallDamageImmune;
        this.hasTouchedGround = allayFallState.hasTouchedGround;
    }

    private boolean hasTouchedGround() {
        return this.hasTouchedGround;
    }

    public boolean hasFallDamageImmunity() {
        return this.fallDamageImmune;
    }

    public void makeFallDamageImmune() {
        this.fallDamageImmune = true;
        this.hasTouchedGround = false;
    }

    public void tick(Entity entity) {
        if (this.fallDamageImmune) {
            entity.fallDistance = 0;

            if (this.hasTouchedGround) {
                this.fallDamageImmune = false;
            } else if (entity.getVelocity().getY() < MathHelper.EPSILON) {
                this.hasTouchedGround = entity.isOnGround();
            }
        }
    }
}
