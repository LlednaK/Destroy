package com.petrolpark.destroy.core.mobeffect;

import com.petrolpark.destroy.DestroyMessages;
import com.petrolpark.destroy.core.player.MobEffectInstanceShaderRemoveS2CPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;

public interface IShaderEffect {
    ResourceLocation getShader();

    default void cleanupShader(LivingEntity pLivingEntity, MobEffect effect) {
        if (pLivingEntity instanceof ServerPlayer) {
            DestroyMessages.sendToClient(new MobEffectInstanceShaderRemoveS2CPacket(effect), (( ServerPlayer ) pLivingEntity));
        }
    }
}
