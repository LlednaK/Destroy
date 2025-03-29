package com.petrolpark.destroy.core.chemistry.hazard.mobeffect;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.core.mobeffect.IShaderEffect;
import com.petrolpark.destroy.core.mobeffect.UncurableMobEffect;
import com.petrolpark.util.RayHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.EntityHitResult;

public class LeadPoisoningMobEffect extends UncurableMobEffect implements IShaderEffect {

    public LeadPoisoningMobEffect() {
        super(MobEffectCategory.HARMFUL, 0x370351);
    };

    @Override
    public void applyEffectTick(LivingEntity livingEntity, int amplifier) {
        super.applyEffectTick(livingEntity, amplifier);
        if (!livingEntity.level().isClientSide() && livingEntity instanceof Player player) {
            player.giveExperiencePoints(-1);
            if (RayHelper.getHitResult(livingEntity, 1f, false) instanceof EntityHitResult result) {
                Entity target = result.getEntity();
                if (target instanceof LivingEntity) {
                    player.attack(target);
                    player.swing(InteractionHand.MAIN_HAND, true);
                };
            };
        };
    };

    @Override
    public void removeAttributeModifiers(LivingEntity pLivingEntity, AttributeMap pAttributeMap, int pAmplifier) {
        IShaderEffect.super.cleanupShader(pLivingEntity, this);
        super.removeAttributeModifiers(pLivingEntity, pAttributeMap, pAmplifier);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % Math.max(1, (int)(500f / (float)(1 + amplifier))) == 0;
    };

    @Override
    public ResourceLocation getShader() {
        return new ResourceLocation(Destroy.MOD_ID, "shaders/post/lead_poisoning.json");
    }
};
