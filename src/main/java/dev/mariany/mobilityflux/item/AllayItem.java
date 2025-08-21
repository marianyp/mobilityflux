package dev.mariany.mobilityflux.item;

import dev.mariany.mobilityflux.MobilityFlux;
import dev.mariany.mobilityflux.entity.allay.EntityWithAllay;
import dev.mariany.mobilityflux.entity.effect.MFStatusEffects;
import net.minecraft.block.BlockState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.passive.AllayEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.NbtWriteView;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ErrorReporter;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class AllayItem extends Item {
    public AllayItem(Settings settings) {
        super(settings);
    }

    public static NbtCompound getData(AllayEntity allayEntity) {
        try (
                ErrorReporter.Logging logging = new ErrorReporter.Logging(
                        allayEntity.getErrorReporterContext(),
                        MobilityFlux.LOGGER
                )
        ) {
            NbtWriteView nbtWriteView = NbtWriteView.create(logging, allayEntity.getRegistryManager());
            allayEntity.saveSelfData(nbtWriteView);
            NbtCompound nbt = nbtWriteView.getNbt();
            nbt.remove("Pos");
            return nbt;
        }
    }

    public static ItemStack fromEntity(AllayEntity allayEntity) {
        return fromData(getData(allayEntity));
    }

    public static ItemStack fromData(NbtCompound allayData) {
        ItemStack stack = MFItems.ALLAY.asItem().getDefaultStack();

        stack.set(DataComponentTypes.ENTITY_DATA, NbtComponent.of(allayData));

        allayData.get("CustomName", TextCodecs.CODEC)
                .ifPresent(customName -> stack.set(DataComponentTypes.CUSTOM_NAME, customName));

        return stack;
    }

    public static AllayEntity toEntity(World world, ItemStack stack, @Nullable LivingEntity user) {
        AllayEntity allayEntity = EntityType.ALLAY.create(world, SpawnReason.SPAWN_ITEM_USE);
        EntityType.copier(world, stack, user).accept(allayEntity);
        return allayEntity;
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (!world.isClient() && user.isSneaking()) {
            if (user instanceof EntityWithAllay entityWithAllay) {
                boolean allayDataEmpty = entityWithAllay.mobilityFlux$getAllay().isEmpty();
                boolean allayFlightPresent = user.hasStatusEffect(MFStatusEffects.ALLAY_FLIGHT);

                if (allayDataEmpty && !allayFlightPresent) {
                    AllayEntity allayEntity = EntityType.ALLAY.create(world, SpawnReason.SPAWN_ITEM_USE);
                    EntityType.copier(world, stack, user).accept(allayEntity);
                    entityWithAllay.mobilityFlux$mount(allayEntity, getSelectedSlot(user, hand));
                    stack.decrement(1);
                    return ActionResult.SUCCESS_SERVER;
                }
            }
        }

        return super.use(world, user, hand);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos blockPos = context.getBlockPos();
        BlockState blockState = world.getBlockState(blockPos);
        Direction direction = context.getSide();
        ItemStack stack = context.getStack();
        PlayerEntity player = context.getPlayer();

        if (world instanceof ServerWorld serverWorld && player != null && !player.isSneaking()) {
            BlockPos spawnPos = blockPos;

            if (!blockState.getCollisionShape(world, blockPos).isEmpty()) {
                spawnPos = blockPos.offset(direction);
            }

            AllayEntity allayEntity = EntityType.ALLAY.spawnFromItemStack(
                    serverWorld,
                    stack,
                    context.getPlayer(),
                    spawnPos,
                    SpawnReason.SPAWN_ITEM_USE,
                    true,
                    !Objects.equals(blockPos, spawnPos) && direction == Direction.UP
            );

            if (allayEntity != null) {
                stack.decrementUnlessCreative(1, player);
                world.emitGameEvent(player, GameEvent.ENTITY_PLACE, blockPos);
            }

            return ActionResult.SUCCESS_SERVER;
        }

        return super.useOnBlock(context);
    }

    private static int getSelectedSlot(PlayerEntity player, Hand hand) {
        if (hand.equals(Hand.OFF_HAND)) {
            return PlayerInventory.OFF_HAND_SLOT;
        }

        return player.getInventory().getSelectedSlot();
    }
}
