package dev.mariany.mobilityflux.entity.gatecrash;

import dev.mariany.mobilityflux.sound.MFSoundEvents;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class GatecrashEntity extends Entity implements Ownable {
    private static final String OWNER_KEY = "Owner";
    private static final String NO_TRAVERSAL_KEY = "NoTraversal";

    private final PositionInterpolator interpolator = new PositionInterpolator(this);
    @Nullable
    protected LazyEntityReference<Entity> owner;
    protected boolean noTraversal = false;
    protected long chunkTicketExpiryTicks = 0L;

    public GatecrashEntity(EntityType<?> type, World world) {
        super(type, world);
        this.intersectionChecked = true;
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
    }

    @Override
    protected void readCustomData(ReadView view) {
        this.setOwner(LazyEntityReference.fromData(view, OWNER_KEY));
        this.noTraversal = view.getBoolean(NO_TRAVERSAL_KEY, false);
    }

    @Override
    protected void writeCustomData(WriteView view) {
        LazyEntityReference.writeData(this.owner, view, OWNER_KEY);
        view.putBoolean(NO_TRAVERSAL_KEY, this.noTraversal);
    }

    @Nullable
    @Override
    public Entity getOwner() {
        return this.owner != null && this.getWorld() instanceof ServerWorld serverWorld
                ? this.owner.resolve(uuid -> resolveOwner(serverWorld, uuid), Entity.class)
                : LazyEntityReference.resolve(this.owner, this.getWorld(), Entity.class);
    }

    @Nullable
    private static Entity resolveOwner(ServerWorld world, UUID uuid) {
        Entity entity = world.getEntity(uuid);
        if (entity != null) {
            return entity;
        }

        for (ServerWorld serverWorld : world.getServer().getWorlds()) {
            if (serverWorld != world) {
                entity = serverWorld.getEntity(uuid);
                if (entity != null) {
                    return entity;
                }
            }
        }

        return null;
    }

    @Override
    public void onRemove(Entity.RemovalReason reason) {
        if (reason != Entity.RemovalReason.UNLOADED_WITH_PLAYER) {
            this.removeFromOwner();
        }

        super.onRemove(reason);
    }

    @Override
    public boolean canUsePortals(boolean allowVehicles) {
        return true;
    }

    @Override
    public int getDefaultPortalCooldown() {
        return 2;
    }

    @Override
    public boolean shouldRender(double distance) {
        if (this.age < 2 && distance < 12.25) {
            return false;
        }

        double visibilityRange = this.getBoundingBox().getAverageSideLength() * 4.0;

        if (Double.isNaN(visibilityRange)) {
            visibilityRange = 4.0;
        }

        visibilityRange *= 64.0;

        return distance < visibilityRange * visibilityRange;
    }

    @Override
    public boolean canHit() {
        return this.isVulnerable();
    }

    @Override
    public boolean damage(ServerWorld world, DamageSource source, float amount) {
        Entity attacker = source.getAttacker();

        if (this.isVulnerable() && attacker != null && attacker.isPlayer()) {
            this.owner = null;
            this.discard();
            return true;
        }

        return false;
    }

    private boolean isVulnerable() {
        if (this.owner == null) {
            return true;
        }

        if (this.getOwner() instanceof EntityWithGatecrashState entityWithGatecrashState) {
            Optional<GatecrashEntity> optionalTether = entityWithGatecrashState.mobilityFlux$getGatecrashState()
                    .getGatecrashEntity();

            if (optionalTether.isPresent()) {
                return !optionalTether.get().equals(this);
            }
        }

        return true;
    }

    @Override
    protected MoveEffect getMoveEffect() {
        return MoveEffect.NONE;
    }

    @Override
    protected double getGravity() {
        return 0.12;
    }

    protected double getForwardSpeed() {
        return 0.1F;
    }

    @Override
    public PositionInterpolator getInterpolator() {
        return this.interpolator;
    }

    @Override
    public void tick() {
        this.interpolator.tick();
        this.tickMovement();
        super.tick();
        this.refreshChunkTicketIfNeeded();
    }

    @Nullable
    @Override
    public Entity teleportTo(TeleportTarget teleportTarget) {
        Entity entity = super.teleportTo(teleportTarget);

        if (entity != null) {
            entity.addPortalChunkTicketAt(BlockPos.ofFloored(entity.getPos()));
        }

        return entity;
    }

    private void tickMovement() {
        if (!this.getWorld().isClient()) {
            this.applyGravity();
            this.applyDrag();
            this.move(MovementType.SELF, this.getVelocity());
            this.applyForwardMovement();
            this.handleHorizontalCollisionDamping();

            if (!this.isOnGround()) {
                this.setVelocity(0, this.getVelocity().y, 0);
            }

            this.tickBlockCollision();

            this.velocityDirty = true;
        }
    }

    private void applyForwardMovement() {
        if (!this.noTraversal && !this.isInsideWall()) {
            double yawRad = Math.toRadians(this.getYaw());
            double forwardX = -Math.sin(yawRad) * this.getForwardSpeed();
            double forwardZ = Math.cos(yawRad) * this.getForwardSpeed();
            this.setVelocity(this.getVelocity().add(forwardX, 0, forwardZ));
        }
    }

    private void handleHorizontalCollisionDamping() {
        if (this.horizontalCollision) {
            Vec3d velocity = this.getVelocity();
            this.setVelocity(velocity.x * 0.2, velocity.y, velocity.z * 0.2);
        }
    }

    private void applyDrag() {
        Vec3d vec3d = this.getVelocity();
        Vec3d vec3d2 = this.getPos();
        float drag = 0.5F;

        if (this.isInFluid()) {
            for (int i = 0; i < 4; i++) {
                float offset = 0.25F;
                this.getWorld()
                        .addParticleClient(ParticleTypes.BUBBLE,
                                vec3d2.x - vec3d.x * offset,
                                vec3d2.y - vec3d.y * offset,
                                vec3d2.z - vec3d.z * offset,
                                vec3d.x,
                                vec3d.y,
                                vec3d.z);
            }

            drag = 0.3F;
        } else if (!this.isOnGround()) {
            drag = 0.8F;
        }

        this.setVelocity(vec3d.multiply(drag));
    }

    private void refreshChunkTicketIfNeeded() {
        int chunkX = ChunkSectionPos.getSectionCoordFloored(this.getPos().getX());
        int chunkZ = ChunkSectionPos.getSectionCoordFloored(this.getPos().getZ());

        if (this.isAlive()) {
            BlockPos blockPos = BlockPos.ofFloored(this.getPos());
            boolean expiredOrMoved =
                    (--this.chunkTicketExpiryTicks <= 0L) ||
                            chunkX != ChunkSectionPos.getSectionCoord(blockPos.getX()) ||
                            chunkZ != ChunkSectionPos.getSectionCoord(blockPos.getZ());

            if (expiredOrMoved) {
                if (this.getOwner() instanceof EntityWithGatecrashState entityWithGatecrashState) {
                    this.chunkTicketExpiryTicks = entityWithGatecrashState.mobilityFlux$getGatecrashState()
                            .handleGatecrash(this);
                }
            }
        }
    }

    public void recallOwner(@Nullable Entity entity) {
        Entity owner = this.getOwner();

        if (entity != null && entity != owner) {
            owner = entity;
            this.setOwner(entity);
        }

        Vec3d pos = this.getPos();

        if (owner != null && this.getWorld() instanceof ServerWorld serverWorld) {
            Entity teleportedOwner = owner.teleportTo(
                    new TeleportTarget(
                            serverWorld,
                            this.getBlockPos().toCenterPos(),
                            Vec3d.ZERO,
                            0F,
                            0F,
                            PositionFlag.combine(PositionFlag.ROT, PositionFlag.DELTA),
                            TeleportTarget.NO_OP
                    )
            );

            if (teleportedOwner != null) {
                teleportedOwner.onLanding();
                teleportedOwner.resetPortalCooldown();
                owner = teleportedOwner;
            }

            if (teleportedOwner instanceof ServerPlayerEntity serverPlayer) {
                serverPlayer.clearCurrentExplosion();
            }

            this.playTeleportSound(serverWorld, pos);
        }

        if (owner instanceof EntityWithGatecrashState entityWithGatecrashState) {
            entityWithGatecrashState.mobilityFlux$getGatecrashState().removeGatecrash();
        }

        this.discard();
    }

    public void setNoTraversal(boolean noTraversal) {
        this.noTraversal = noTraversal;
    }

    public void setOwner(Entity owner) {
        setOwner(new LazyEntityReference<>(owner));
    }

    public void setOwner(@Nullable LazyEntityReference<Entity> owner) {
        this.removeFromOwner();
        this.owner = owner;
        this.addToOwner();
    }

    private void removeFromOwner() {
        if (this.getOwner() instanceof EntityWithGatecrashState entityWithGatecrashState) {
            entityWithGatecrashState.mobilityFlux$getGatecrashState().removeGatecrash();
        }
    }

    private void addToOwner() {
        if (this.getOwner() instanceof EntityWithGatecrashState entityWithGatecrashState) {
            entityWithGatecrashState.mobilityFlux$getGatecrashState().setGatecrashEntity(this);
        }
    }

    private void playTeleportSound(World world, Vec3d pos) {
        world.playSound(null, pos.x, pos.y, pos.z, MFSoundEvents.ITEM_GATECRASH_RETURN, SoundCategory.PLAYERS);
    }
}
