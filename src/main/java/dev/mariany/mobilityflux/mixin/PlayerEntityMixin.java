package dev.mariany.mobilityflux.mixin;

import dev.mariany.mobilityflux.entity.allay.AllayFallState;
import dev.mariany.mobilityflux.entity.allay.EntityWithAllay;
import dev.mariany.mobilityflux.entity.effect.MFStatusEffects;
import dev.mariany.mobilityflux.entity.gatecrash.EntityWithGatecrashState;
import dev.mariany.mobilityflux.entity.gatecrash.GatecrashState;
import dev.mariany.mobilityflux.item.AllayItem;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.passive.AllayEntity;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements EntityWithGatecrashState, EntityWithAllay {
    @Unique
    private static final String ALLAY_FALL_STATE_KEY = "AllayFallState";

    @Shadow
    @Final
    private ItemCooldownManager itemCooldownManager;

    @Shadow
    public abstract PlayerInventory getInventory();

    @Shadow
    @Final
    private PlayerInventory inventory;
    @Unique
    private static final TrackedData<Boolean> TETHERED = DataTracker.registerData(
            PlayerEntityMixin.class,
            TrackedDataHandlerRegistry.BOOLEAN
    );

    @Unique
    private static final TrackedData<NbtCompound> ALLAY = DataTracker.registerData(
            PlayerEntityMixin.class,
            TrackedDataHandlerRegistry.NBT_COMPOUND
    );

    @Unique
    private final GatecrashState gatecrashState = new GatecrashState(TETHERED, this::getWorld, this::getDataTracker);

    @Unique
    private final AllayFallState allayFallState = new AllayFallState();

    @Unique
    private int allayReturnSlot = -1;

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public GatecrashState mobilityFlux$getGatecrashState() {
        return this.gatecrashState;
    }

    @Override
    public AllayFallState mobilityFlux$getAllayFallState() {
        return this.allayFallState;
    }

    @Override
    public NbtCompound mobilityFlux$getAllay() {
        return this.dataTracker.get(ALLAY);
    }

    @Override
    public void mobilityFlux$setAllay(NbtCompound allayData) {
        this.dataTracker.set(ALLAY, allayData);
    }

    @Override
    public boolean mobilityFlux$mount(AllayEntity allayEntity) {
        return this.mobilityFlux$mount(allayEntity, -1);
    }

    @Override
    public boolean mobilityFlux$mount(AllayEntity allayEntity, int slot) {
        if (!this.getWorld().isClient() && !this.hasVehicle()) {
            this.mobilityFlux$setAllay(AllayItem.getData(allayEntity));

            allayEntity.discard();

            this.mobilityFlux$setReturnSlot(slot);

            this.addStatusEffect(
                    new StatusEffectInstance(
                            MFStatusEffects.ALLAY_FLIGHT,
                            MathHelper.nextBetween(this.random, 30, 45) * 20,
                            0,
                            false,
                            true
                    )
            );
        }

        return false;
    }

    @Override
    public void mobilityFlux$dismount(boolean cooldown) {
        NbtCompound allayData = this.dataTracker.get(ALLAY);

        if (!allayData.isEmpty()) {
            ItemStack stack = AllayItem.fromData(allayData);

            if (cooldown) {
                this.itemCooldownManager.set(stack, MathHelper.nextBetween(this.random, 10, 30) * 20);
            }

            boolean inserted = false;

            if (this.allayReturnSlot > -1) {
                if (this.inventory.getStack(this.allayReturnSlot).isEmpty()) {
                    this.inventory.setStack(this.allayReturnSlot, stack);
                    inserted = true;
                }
            }

            if (!inserted) {
                this.giveOrDropStack(stack);
            }
        }

        this.dataTracker.set(ALLAY, new NbtCompound());
    }

    @Override
    public int mobilityFlux$getReturnSlot() {
        return this.allayReturnSlot;
    }

    @Override
    public void mobilityFlux$setReturnSlot(int slot) {
        this.allayReturnSlot = slot;
    }

    @Inject(method = "initDataTracker", at = @At(value = "TAIL"))
    protected void injectInitDataTracker(DataTracker.Builder builder, CallbackInfo ci) {
        builder.add(TETHERED, false);
        builder.add(ALLAY, new NbtCompound());
    }

    @Inject(method = "writeCustomData", at = @At(value = "TAIL"))
    protected void injectWriteCustomData(WriteView view, CallbackInfo ci) {
        view.put(ALLAY_FALL_STATE_KEY, AllayFallState.CODEC, this.allayFallState);
        gatecrashState.writeGatecrash(view);
        EntityWithAllay.writeCustomData(view, this);
    }

    @Inject(method = "readCustomData", at = @At(value = "TAIL"))
    protected void injectReadCustomData(ReadView view, CallbackInfo ci) {
        view.read(ALLAY_FALL_STATE_KEY, AllayFallState.CODEC).ifPresent(this.allayFallState::applyState);
        gatecrashState.readGatecrash(view);
        EntityWithAllay.readCustomData(view, this);
    }

    @Inject(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    shift = At.Shift.AFTER,
                    target = "Lnet/minecraft/entity/LivingEntity;tick()V"
            )
    )
    protected void injectTick(CallbackInfo ci) {
        this.allayFallState.tick(this);
    }
}
