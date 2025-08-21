package dev.mariany.mobilityflux.item;

import dev.mariany.mobilityflux.entity.MFEntities;
import dev.mariany.mobilityflux.entity.gatecrash.EntityWithGatecrashState;
import dev.mariany.mobilityflux.entity.gatecrash.GatecrashEntity;
import dev.mariany.mobilityflux.entity.gatecrash.GatecrashState;
import dev.mariany.mobilityflux.sound.MFSoundEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Optional;

public class GatecrashItem extends Item {
    public GatecrashItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient && user instanceof EntityWithGatecrashState entityWithGatecrashState) {
            GatecrashState state = entityWithGatecrashState.mobilityFlux$getGatecrashState();
            Optional<GatecrashEntity> optionalGatecrashEntity = state.getGatecrashEntity();

            if (optionalGatecrashEntity.isPresent()) {
                GatecrashEntity gatecrashEntity = optionalGatecrashEntity.get();

                if (user.isSneaking()) {
                    gatecrashEntity.discard();

                    if (user instanceof ServerPlayerEntity serverPlayer) {
                        serverPlayer.playSoundToPlayer(
                                MFSoundEvents.ITEM_GATECRASH_DESTROY,
                                SoundCategory.NEUTRAL,
                                1F,
                                1F
                        );
                    }

                    return ActionResult.SUCCESS_SERVER;
                }

                gatecrashEntity.recallOwner(user);
                user.getStackInHand(hand).damage(1, user, LivingEntity.getSlotForHand(hand));

                return ActionResult.SUCCESS_SERVER;
            }

            if (createGatecrash(user, user.getPos()).isPresent()) {
                return ActionResult.SUCCESS_SERVER;
            }
        }

        return ActionResult.PASS;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        PlayerEntity user = context.getPlayer();

        if (user instanceof EntityWithGatecrashState entityWithGatecrashState) {
            GatecrashState state = entityWithGatecrashState.mobilityFlux$getGatecrashState();

            if (!state.isTethered()) {
                World world = context.getWorld();
                BlockPos blockPos = context.getBlockPos();

                if (!world.getBlockState(blockPos).getCollisionShape(world, blockPos).isEmpty()) {
                    blockPos = blockPos.offset(context.getSide());
                }

                Vec3d pos = blockPos.toCenterPos().subtract(0, 0.5, 0);

                if (!context.getWorld().isClient && createGatecrash(user, pos).isPresent()) {
                    return ActionResult.SUCCESS_SERVER;
                }

                return ActionResult.CONSUME;
            }
        }

        return ActionResult.PASS;
    }

    private static Optional<GatecrashEntity> createGatecrash(PlayerEntity player, Vec3d pos) {
        World world = player.getWorld();
        GatecrashEntity gatecrashEntity = MFEntities.GATECRASH.create(world, SpawnReason.SPAWN_ITEM_USE);

        if (gatecrashEntity != null) {
            gatecrashEntity.refreshPositionAndAngles(
                    pos.getX(),
                    pos.getY(),
                    pos.getZ(),
                    player.getYaw(),
                    0
            );

            gatecrashEntity.playSound(MFSoundEvents.ITEM_GATECRASH_RELEASE, 1F, 1F);

            gatecrashEntity.setNoTraversal(player.isSneaking());
            gatecrashEntity.setOwner(player);
            world.spawnEntity(gatecrashEntity);

            return Optional.of(gatecrashEntity);
        }

        return Optional.empty();
    }
}
