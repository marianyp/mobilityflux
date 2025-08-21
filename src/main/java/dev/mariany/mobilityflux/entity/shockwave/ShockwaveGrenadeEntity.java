package dev.mariany.mobilityflux.entity.shockwave;

import com.mojang.serialization.Codec;
import dev.mariany.mobilityflux.entity.MFEntities;
import dev.mariany.mobilityflux.item.MFItems;
import dev.mariany.mobilityflux.sound.MFSoundEvents;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.explosion.AdvancedExplosionBehavior;
import net.minecraft.world.explosion.ExplosionBehavior;

import java.util.Optional;

public class ShockwaveGrenadeEntity extends ThrownItemEntity {
    private static final String FUSE_KEY = "Fuse";
    private static final float FUSE_SECONDS = 0.45F;
    private static final ExplosionBehavior EXPLOSION_BEHAVIOR = new AdvancedExplosionBehavior(
            false,
            false,
            Optional.of(3.25F),
            Optional.empty()
    );

    private int fuse;

    public ShockwaveGrenadeEntity(EntityType<? extends ShockwaveGrenadeEntity> entityType, World world) {
        super(entityType, world);
    }

    public ShockwaveGrenadeEntity(World world, LivingEntity owner, ItemStack stack) {
        super(MFEntities.SHOCKWAVE_GRENADE, owner, world, stack);
    }

    public ShockwaveGrenadeEntity(World world, double x, double y, double z, ItemStack stack) {
        super(MFEntities.SHOCKWAVE_GRENADE, x, y, z, world, stack);
    }

    @Override
    public void tick() {
        HitResult hitResult = ProjectileUtil.getCollision(this, this::canHit);

        if (hitResult.getType().equals(HitResult.Type.MISS)) {
            super.tick();
        } else {
            this.triggerFuse();
        }

        if (fuse > 0 && --fuse == 0) {
            this.getWorld()
                    .createExplosion(
                            this,
                            null,
                            EXPLOSION_BEHAVIOR,
                            this.getX(),
                            this.getY(),
                            this.getZ(),
                            4.5F,
                            false,
                            World.ExplosionSourceType.NONE,
                            ParticleTypes.GUST_EMITTER_SMALL,
                            ParticleTypes.GUST_EMITTER_LARGE,
                            MFSoundEvents.ENTITY_SHOCKWAVE_GRENADE_EXPLODE
                    );
            this.discard();
        }
    }

    @Override
    protected Item getDefaultItem() {
        return MFItems.SHOCKWAVE_GRENADE;
    }

    @Override
    protected void writeCustomData(WriteView view) {
        super.writeCustomData(view);
        view.put(FUSE_KEY, Codec.INT, this.fuse);
    }

    @Override
    protected void readCustomData(ReadView view) {
        super.readCustomData(view);
        this.fuse = view.read(FUSE_KEY, Codec.INT).orElse(0);
    }

    private void triggerFuse() {
        if (this.fuse <= 0) {
            this.fuse = MathHelper.floor(FUSE_SECONDS * 20);
            this.playSound(
                    MFSoundEvents.ENTITY_SHOCKWAVE_GRENADE_PRIME,
                    1F,
                    MathHelper.nextBetween(this.random, 0.9F, 1F)
            );
        }
    }
}
