package com.petrolpark.destroy.mixin.client;

import com.petrolpark.destroy.DestroyPostUniforms;
import net.minecraft.client.renderer.EffectInstance;
import net.minecraft.client.renderer.PostPass;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PostPass.class)
public abstract class PostPassMixin {
    @Shadow @Final private EffectInstance effect;

    @Inject(method = "process", at = @At(value = "HEAD"))
    public void inProcess(float pPartialTicks, CallbackInfo ci) {
        for ( DestroyPostUniforms postUniform : DestroyPostUniforms.values() ) {
            postUniform.applyUniform(this.effect.safeGetUniform(postUniform.getName()));
        }
    }
}
