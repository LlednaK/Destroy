package com.petrolpark.destroy;

import com.mojang.blaze3d.shaders.AbstractUniform;

import java.util.function.Consumer;

public enum DestroyPostUniforms {
    EFFECT_FACTOR("EffectFactor");

    final String name;
    Consumer<AbstractUniform> onUpdate;

    DestroyPostUniforms(String name, Consumer<AbstractUniform> onUpdate) {
        this.name = name;
        this.onUpdate = onUpdate;
    }

    DestroyPostUniforms(String name) {
        this.name = name;
        this.onUpdate = (uniform) -> {};
    }

    public void update(Consumer<AbstractUniform> onUpdate) {
        this.onUpdate = onUpdate;
    }

    public void applyUniform(AbstractUniform uniform) {
        this.onUpdate.accept(uniform);
    }

    public String getName() {
        return this.name;
    }
}
