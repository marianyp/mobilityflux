package dev.mariany.mobilityflux.event.entity;

import dev.mariany.mobilityflux.entity.allay.EntityWithAllay;
import dev.mariany.mobilityflux.item.AllayItem;
import dev.mariany.mobilityflux.item.MFItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AllayEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class UseEntityHandler {
    public static ActionResult onUseEntity(
            PlayerEntity player,
            World world,
            Hand hand,
            Entity entity,
            @Nullable EntityHitResult entityHitResult
    ) {
        if (!world.isClient()) {
            if (entity instanceof AllayEntity allayEntity && player instanceof EntityWithAllay entityWithAllay) {
                boolean handEmpty = player.getMainHandStack().isEmpty() && player.getOffHandStack().isEmpty();
                boolean allayHandEmpty = !allayEntity.isHoldingItem();
                boolean isAllayPresent = entityWithAllay.mobilityFlux$getAllay().isEmpty();
                boolean isOnCooldown = player.getItemCooldownManager().isCoolingDown(MFItems.ALLAY.getDefaultStack());

                if (handEmpty && allayHandEmpty) {
                    if (player.isSneaking()) {
                        if (isAllayPresent && !isOnCooldown && entityWithAllay.mobilityFlux$mount(allayEntity)) {
                            return ActionResult.SUCCESS;
                        }
                    } else {
                        ItemStack stack = AllayItem.fromEntity(allayEntity);
                        player.setStackInHand(hand, stack);
                        allayEntity.discard();
                        return ActionResult.SUCCESS;
                    }
                }
            }
        }


        return ActionResult.PASS;
    }
}
