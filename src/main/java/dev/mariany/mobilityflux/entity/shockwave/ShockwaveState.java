package dev.mariany.mobilityflux.entity.shockwave;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.TintedParticleEffect;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class ShockwaveState {
    private static final String HAS_TOUCHED_GROUND_KEY = "HasTouchedGround";
    private static final String LOW_GRAVITY_TICKS_KEY = "LowGravityTicks";
    private static final String REMAINING_JUMPS_KEY = "RemainingJumps";

    private static final float IMMUNITY_SECONDS = 2.5F;
    private static final int INITIAL_REMAINING_JUMPS = 2;
    private static final double SHOCKWAVE_BOUNCE_FACTOR = 0.935F;

    public static final Codec<ShockwaveState> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            Codec.BOOL.optionalFieldOf(HAS_TOUCHED_GROUND_KEY, false)
                                    .forGetter(ShockwaveState::hasTouchedGround),
                            Codec.INT.optionalFieldOf(LOW_GRAVITY_TICKS_KEY, 0)
                                    .forGetter(ShockwaveState::getLowGravityTicks),
                            Codec.INT.optionalFieldOf(REMAINING_JUMPS_KEY, INITIAL_REMAINING_JUMPS)
                                    .forGetter(ShockwaveState::getRemainingJumps)
                    )
                    .apply(instance, ShockwaveState::new)
    );

    private boolean hasTouchedGround;
    private int lowGravityTicks;
    private int remainingJumps;

    @Nullable
    private Vec3d previousVelocity = null;

    public ShockwaveState() {
        this(false, 0, INITIAL_REMAINING_JUMPS);
    }

    public ShockwaveState(boolean hasTouchedGround, int lowGravityTicks, int remainingJumps) {
        this.hasTouchedGround = hasTouchedGround;
        this.lowGravityTicks = lowGravityTicks;
        this.remainingJumps = remainingJumps;
    }

    private boolean hasTouchedGround() {
        return this.hasTouchedGround;
    }

    private int getLowGravityTicks() {
        return this.lowGravityTicks;
    }

    private int getRemainingJumps() {
        return this.remainingJumps;
    }

    public void applyState(ShockwaveState state) {
        this.hasTouchedGround = state.hasTouchedGround;
        this.lowGravityTicks = state.lowGravityTicks;
        this.remainingJumps = state.remainingJumps;
    }

    private boolean shouldCancel(LivingEntity entity) {
        return entity.isSpectator() || entity.hasLandedInFluid() || entity.isGliding() || entity.isClimbing() ||
                (entity instanceof PlayerEntity player && player.getAbilities().flying);
    }

    public void tick(LivingEntity entity) {
        if (this.lowGravityTicks > 0) {
            if (shouldCancel(entity)) {
                reset();
                return;
            }

            if (entity.getVelocity().getY() < MathHelper.EPSILON && !this.hasTouchedGround) {
                this.hasTouchedGround = entity.isOnGround();
            }

            if (hasLowGravity()) {
                entity.fallDistance = 0;
            }

            applyShockwaveBounce(entity);

            if (this.hasTouchedGround) {
                if (--this.lowGravityTicks <= 0) {
                    reset();
                }
            }

            addShockwaveParticles(entity);
        }
    }

    public boolean hasLowGravity() {
        return this.lowGravityTicks > 0;
    }

    public boolean hasLowGravityBounce() {
        return this.hasLowGravity() && this.remainingJumps > 0;
    }

    public void reset() {
        this.hasTouchedGround = false;
        this.lowGravityTicks = 0;
        this.remainingJumps = INITIAL_REMAINING_JUMPS;
    }

    public void startImmunity() {
        reset();
        this.lowGravityTicks = MathHelper.floor(IMMUNITY_SECONDS * 20);
    }

    public void onJump() {
        if (hasLowGravityBounce()) {
            this.remainingJumps = Math.max(0, this.remainingJumps - 1);
        }
    }

    public void applyShockwaveBounce(LivingEntity entity) {
        Vec3d velocity = entity.getVelocity();

        if (!entity.isOnGround() && this.horizontalVelocityChanged(entity) && this.hasLowGravityBounce()) {
            entity.setVelocity(
                    velocity.x / SHOCKWAVE_BOUNCE_FACTOR,
                    velocity.y,
                    velocity.z / SHOCKWAVE_BOUNCE_FACTOR
            );

            entity.velocityDirty = true;
            this.previousVelocity = entity.getVelocity();
        }
    }

    private boolean horizontalVelocityChanged(LivingEntity entity) {
        Vec3d velocity = entity.getVelocity();

        if (this.previousVelocity != null) {
            return this.previousVelocity.x != velocity.x || this.previousVelocity.z != velocity.z;
        }

        return true;
    }

    private void addShockwaveParticles(LivingEntity entity) {
        if (this.hasLowGravityBounce()) {
            if (entity.getRandom().nextInt(2) == 0) {
                entity.getWorld().addParticleClient(
                        TintedParticleEffect.create(
                                ParticleTypes.ENTITY_EFFECT,
                                0.553F,
                                0.416F,
                                0.8F
                        ),
                        entity.getParticleX(0.5),
                        entity.getRandomBodyY(),
                        entity.getParticleZ(0.5),
                        1,
                        1,
                        1
                );
            }
        }
    }
}
