package dev.mariany.mobilityflux.item;

import dev.mariany.mobilityflux.entity.shockwave.ShockwaveGrenadeEntity;
import dev.mariany.mobilityflux.sound.MFSoundEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ProjectileItem;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.world.World;

public class ShockwaveGrenadeItem extends Item implements ProjectileItem {
    public static float POWER = 0.8F;

    public ShockwaveGrenadeItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);

        world.playSound(
                null,
                user.getX(),
                user.getY(),
                user.getZ(),
                MFSoundEvents.ITEM_SHOCKWAVE_GRENADE_THROW,
                SoundCategory.NEUTRAL,
                0.5F,
                0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F)
        );

        if (world instanceof ServerWorld serverWorld) {
            ProjectileEntity.spawnWithVelocity(
                    ShockwaveGrenadeEntity::new,
                    serverWorld,
                    itemStack,
                    user,
                    0F,
                    POWER,
                    1F
            );
        }

        user.incrementStat(Stats.USED.getOrCreateStat(this));
        itemStack.decrementUnlessCreative(1, user);

        return ActionResult.SUCCESS;
    }

    @Override
    public ProjectileEntity createEntity(World world, Position pos, ItemStack stack, Direction direction) {
        return new ShockwaveGrenadeEntity(world, pos.getX(), pos.getY(), pos.getZ(), stack);
    }
}
