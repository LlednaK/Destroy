package com.petrolpark.petrolsparts.mixin.plugin;

import com.petrolpark.mixin.plugin.PetrolparkMixinPlugin;

public class PetrolsPartsMixinPlugin extends PetrolparkMixinPlugin {
    
    @Override
    protected String getMixinPackage() {
        return "com.petrolpark.petrolsparts.mixin";
    };
};
