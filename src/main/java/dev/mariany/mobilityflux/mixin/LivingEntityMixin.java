package dev.mariany.mobilityflux.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.mariany.mobilityflux.MFUtil;
import dev.mariany.mobilityflux.entity.MFEntities;
import dev.mariany.mobilityflux.entity.allay.EntityWithAllay;
import dev.mariany.mobilityflux.entity.effect.ExtendedStatusEffect;
import dev.mariany.mobilityflux.entity.effect.MFStatusEffects;
import dev.mariany.mobilityflux.entity.shockwave.EntityWithShockwaveState;
import dev.mariany.mobilityflux.entity.shockwave.ShockwaveState;
import dev.mariany.mobilityflux.packet.clientbound.StartShockwaveImmunity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements EntityWithShockwaveState {
    @Unique
    private static final String SHOCKWAVE_STATE_KEY = "ShockwaveState";

    @Unique
    private final ShockwaveState shockwaveState = new ShockwaveState();

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "writeCustomData", at = @At(value = "TAIL"))
    protected void injectWriteCustomData(WriteView view, CallbackInfo ci) {
        view.put(SHOCKWAVE_STATE_KEY, ShockwaveState.CODEC, this.shockwaveState);
    }

    @Inject(method = "readCustomData", at = @At(value = "TAIL"))
    protected void injectReadCustomData(ReadView view, CallbackInfo ci) {
        view.read(SHOCKWAVE_STATE_KEY, ShockwaveState.CODEC).ifPresent(this.shockwaveState::applyState);
    }

    @Inject(method = "tick", at = @At(value = "TAIL"))
    public void injectTick(CallbackInfo ci) {
        LivingEntity livingEntity = (LivingEntity) (Object) this;
        shockwaveState.tick(livingEntity);
    }

    @Inject(method = "handleFallDamage", at = @At(value = "HEAD"), cancellable = true)
    public void injectHandleFallDamage(
            double fallDistance,
            float damagePerDistance,
            DamageSource damageSource,
            CallbackInfoReturnable<Boolean> cir
    ) {
        if (MFUtil.hasFallDamageImmunity(this)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(
            method = "jump",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/LivingEntity;setVelocity(DDD)V"
            )
    )
    public void injectJump(CallbackInfo ci) {
        LivingEntity livingEntity = ((LivingEntity) (Object) this);

        if (livingEntity instanceof EntityWithShockwaveState entityWithShockwaveState) {
            entityWithShockwaveState.mobilityFlux$getShockwaveState().onJump();
        }
    }

    @Inject(
            method = "canHaveStatusEffect",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    public void injectCanHaveStatusEffect(StatusEffectInstance effect, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity livingEntity = ((LivingEntity) (Object) this);

        if (!this.getWorld().isClient() && effect.equals(MFStatusEffects.ALLAY_FLIGHT)) {
            boolean valid = false;

            if (livingEntity instanceof EntityWithAllay entityWithAllay) {
                valid = !entityWithAllay.mobilityFlux$getAllay().isEmpty();
            }

            cir.setReturnValue(valid);
        }
    }

    @Inject(
            method = "onStatusEffectUpgraded",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/effect/StatusEffect;onRemoved(Lnet/minecraft/entity/attribute/AttributeContainer;)V"
            )
    )
    protected void injectOnStatusEffectUpgraded(
            StatusEffectInstance effect,
            boolean reapplyEffect,
            Entity source,
            CallbackInfo ci
    ) {
        handleStatusEffectRemove(effect.getEffectType().value(), true);
    }

    @WrapOperation(
            method = "onStatusEffectsRemoved",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/effect/StatusEffect;onRemoved(Lnet/minecraft/entity/attribute/AttributeContainer;)V"
            )
    )
    protected void wrapOnStatusEffectsRemoved(
            StatusEffect effect, AttributeContainer attributeContainer, Operation<Void> original
    ) {
        handleStatusEffectRemove(effect, false);
        original.call(effect, attributeContainer);
    }

    @Override
    public void onExplodedBy(@Nullable Entity entity) {
        super.onExplodedBy(entity);

        if (entity != null && entity.getType() == MFEntities.SHOCKWAVE_GRENADE) {
            LivingEntity livingEntity = ((LivingEntity) (Object) this);

            if (!livingEntity.getWorld().isClient()) {
                this.shockwaveState.startImmunity();
                MFUtil.sendToEntityAndTrackers(livingEntity, new StartShockwaveImmunity(livingEntity.getId()));
            }
        }
    }

    @Override
    public ShockwaveState mobilityFlux$getShockwaveState() {
        return shockwaveState;
    }

    @Unique
    private void handleStatusEffectRemove(StatusEffect effect, boolean upgrade) {
        LivingEntity livingEntity = (LivingEntity) (Object) this;

        if (effect instanceof ExtendedStatusEffect extendedStatusEffect) {
            extendedStatusEffect.mobilityFlux$onRemoved(livingEntity, upgrade);
        }
    }
}
