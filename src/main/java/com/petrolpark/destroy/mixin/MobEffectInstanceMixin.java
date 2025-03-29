package com.petrolpark.destroy.mixin;

import com.petrolpark.destroy.DestroyMessages;
import com.petrolpark.destroy.DestroyPostUniforms;
import com.petrolpark.destroy.core.mobeffect.IShaderEffect;
import com.petrolpark.destroy.core.player.SyncInitialDurationS2CPacket;
import com.petrolpark.destroy.util.IGameRendererMixin;
import com.petrolpark.destroy.util.IMobEffectInstanceMixin;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Optional;

@Mixin( MobEffectInstance.class)
public abstract class MobEffectInstanceMixin implements IMobEffectInstanceMixin, Comparable<MobEffectInstance>, net.minecraftforge.common.extensions.IForgeMobEffectInstance{
    @Shadow
    int duration;

    @Shadow
    private MobEffect effect;

    public int initialDuration;
    private boolean sentInitialDuration = false;

    @Shadow
    protected static MobEffectInstance readCurativeItems(MobEffectInstance effect, CompoundTag nbt) {
        throw new AbstractMethodError("Shadow");
    };

    private boolean shouldUpdateUniform = false;
    private boolean shaderInitialized = false;

    @Inject(method = "<init>(Lnet/minecraft/world/effect/MobEffect;IIZZZLnet/minecraft/world/effect/MobEffectInstance;Ljava/util/Optional;)V", at = @At("RETURN"))
    private void onInitialize(CallbackInfo ci) {
        initialDuration = duration;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void inTick(LivingEntity pEntity, Runnable pOnExpirationRunnable, CallbackInfoReturnable<Boolean> ci) {
        if (!pEntity.level().isClientSide() && !sentInitialDuration && pEntity instanceof ServerPlayer ) {
            DestroyMessages.sendToClient(new SyncInitialDurationS2CPacket(this.initialDuration, this.effect), (( ServerPlayer ) pEntity));
        }
        if (pEntity.level().isClientSide()) {
            IGameRendererMixin gameRenderer = ( IGameRendererMixin ) Minecraft.getInstance().gameRenderer;
            if (effect instanceof IShaderEffect) {
                if (!shaderInitialized) {
                    shaderInitialized = true;
                    shouldUpdateUniform = true;
                    gameRenderer.addMobEffectInstanceShader((( IShaderEffect ) effect).getShader(), ((MobEffectInstance) (Object) this));
                    System.out.println("--------------------------------");
                }
            }
        }
    }

    @Inject(method = "writeDetailsTo", at = @At("TAIL"))
    public void inWriteDetailsTo(CompoundTag pNbt, CallbackInfo ci) {
        if (this.effect instanceof IShaderEffect) {
            pNbt.putInt("InitialDuration", initialDuration);
        }
    }

    @Inject(method = "loadSpecifiedEffect", at = @At("RETURN"), cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT)
    private static void inLoadSpecifiedEffect(MobEffect pEffect, CompoundTag pNbt, CallbackInfoReturnable<MobEffectInstance> ci, int i, int j, boolean flag, boolean flag1, boolean flag2, MobEffectInstance mobeffectinstance, Optional<MobEffectInstance.FactorData> optional) {
        int initialDuration = pNbt.getInt("InitialDuration");
        MobEffectInstance inMobEffectinstance = readCurativeItems(new MobEffectInstance(pEffect, j, Math.max(0, i), flag, flag1, flag2, mobeffectinstance, optional), pNbt);
        System.out.println("Got initial duration of " + initialDuration + " with duration of " + j );
        if (inMobEffectinstance.getEffect() instanceof  IShaderEffect) {
            if (initialDuration > 0) {
                (( IMobEffectInstanceMixin ) inMobEffectinstance).setInitialDuration(initialDuration);
            } else {
                (( IMobEffectInstanceMixin ) inMobEffectinstance).setInitialDuration(1);
            }
        }
        ci.setReturnValue(inMobEffectinstance);
        ci.cancel();
    }

    @Override
    public void updateUniforms() {
        if (duration >= 0) {
            DestroyPostUniforms.EFFECT_FACTOR.update((uniform) -> {
                uniform.set((float) duration /  initialDuration);
            });
        } else {
            DestroyPostUniforms.EFFECT_FACTOR.update((uniform) -> {
                uniform.set(0.5f);
            });
        }
    }

    @Override
    public void setInitialDuration(int initialDuration) {
        this.initialDuration = initialDuration;
    }
}
