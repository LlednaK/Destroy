package com.petrolpark.destroy.world.damage;

import com.petrolpark.destroy.world.explosion.SmartExplosion;

import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;

public class SmartExplosionDamageSource extends DamageSource {

    public final SmartExplosion explosion;

    public SmartExplosionDamageSource(Holder<DamageType> type, SmartExplosion explosion) {
        super(type, explosion.getDirectSourceEntity(), explosion.getIndirectSourceEntity(), explosion.getPosition());
        this.explosion = explosion;
    };

};
