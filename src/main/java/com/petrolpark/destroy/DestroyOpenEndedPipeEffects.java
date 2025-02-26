package com.petrolpark.destroy;

import com.petrolpark.destroy.core.fluid.openpipeeffect.BurningOpenEndedPipeEffect;
import com.petrolpark.destroy.core.fluid.openpipeeffect.EffectApplyingOpenEndedPipeEffect;
import com.petrolpark.destroy.core.pollution.PollutingOpenEndedPipeEffect;
import com.simibubi.create.content.fluids.OpenEndedPipe;

import net.minecraft.world.effect.MobEffectInstance;

public class DestroyOpenEndedPipeEffects {
  
    public static final void register() {
        OpenEndedPipe.registerEffectHandler(new PollutingOpenEndedPipeEffect());
        OpenEndedPipe.registerEffectHandler(new BurningOpenEndedPipeEffect(DestroyFluids.NAPALM_SUNDAE.get()));
        OpenEndedPipe.registerEffectHandler(new BurningOpenEndedPipeEffect(DestroyFluids.MOLTEN_CINNABAR.get()));
        OpenEndedPipe.registerEffectHandler(new EffectApplyingOpenEndedPipeEffect(new MobEffectInstance(DestroyMobEffects.FRAGRANCE.get(), 21, 0, false, false, true), DestroyFluids.PERFUME.get()));
        OpenEndedPipe.registerEffectHandler(new EffectApplyingOpenEndedPipeEffect(new MobEffectInstance(DestroyMobEffects.INEBRIATION.get(), 21, 0, false, false, true), DestroyFluids.UNDISTILLED_MOONSHINE.get()));
        OpenEndedPipe.registerEffectHandler(new EffectApplyingOpenEndedPipeEffect(new MobEffectInstance(DestroyMobEffects.INEBRIATION.get(), 21, 2, false, false, true), DestroyFluids.MOONSHINE.get()));
    };
};
