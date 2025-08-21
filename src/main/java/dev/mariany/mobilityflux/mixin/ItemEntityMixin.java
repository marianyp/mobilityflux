package dev.mariany.mobilityflux.mixin;

import dev.mariany.mobilityflux.item.AllayItem;
import dev.mariany.mobilityflux.item.MFItems;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.passive.AllayEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {
    @Inject(method = "tick", at = @At(value = "HEAD"), cancellable = true)
    public void injectTick(CallbackInfo ci) {
        ItemEntity itemEntity = (ItemEntity) (Object) this;
        World world = itemEntity.getWorld();
        ItemStack stack = itemEntity.getStack();

        if (!world.isClient() && stack.isOf(MFItems.ALLAY)) {
            AllayEntity allayEntity = AllayItem.toEntity(world, stack, null);
            allayEntity.setPosition(itemEntity.getPos());
            allayEntity.setVelocity(itemEntity.getVelocity());
            world.spawnEntity(allayEntity);
            itemEntity.discard();
            ci.cancel();
        }
    }
}
