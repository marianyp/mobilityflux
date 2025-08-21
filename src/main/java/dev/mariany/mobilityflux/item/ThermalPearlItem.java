package dev.mariany.mobilityflux.item;

import dev.mariany.mobilityflux.sound.MFSoundEvents;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class ThermalPearlItem extends Item {
    private static final int EFFECT_SECONDS = 10;
    private static final int COOLDOWN_SECONDS = 25;

    public ThermalPearlItem(Settings settings) {
        super(settings);
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }

    @Override
    public ActionResult use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        ItemCooldownManager cooldownManager = player.getItemCooldownManager();

        player.addStatusEffect(
                new StatusEffectInstance(
                        StatusEffects.FIRE_RESISTANCE,
                        EFFECT_SECONDS * 20,
                        0,
                        false,
                        true,
                        true
                )
        );

        player.addStatusEffect(
                new StatusEffectInstance(
                        StatusEffects.SPEED,
                        EFFECT_SECONDS * 20,
                        0,
                        false,
                        true,
                        true
                )
        );

        player.addStatusEffect(
                new StatusEffectInstance(
                        StatusEffects.HASTE,
                        EFFECT_SECONDS * 20,
                        1,
                        false,
                        true,
                        true
                )
        );

        cooldownManager.set(stack, COOLDOWN_SECONDS * 20);
        stack.damage(1, player);

        this.playUseSound(world, player.getBlockPos());

        return ActionResult.SUCCESS;
    }

    private void playUseSound(World world, BlockPos pos) {
        Random random = world.getRandom();
        world.playSound(
                null,
                pos,
                MFSoundEvents.ITEM_THERMAL_PEARL_USE,
                SoundCategory.BLOCKS,
                1F,
                (random.nextFloat() - random.nextFloat()) * 0.2F + 1F
        );
    }
}

