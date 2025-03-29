package com.petrolpark.petrolsparts.core;

import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.util.nullness.NonNullFunction;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;

public class PetrolsPartsRegistrate extends CreateRegistrate {

    public PetrolsPartsRegistrate(String modid) {
        super(modid);
    };

    @Override
    public <T extends Item, P> ItemBuilder<T, P> item(P parent, String name, NonNullFunction<Properties, T> factory) {
        return entry(name, callback -> ItemBuilder.create(this, parent, name, callback, factory)); // Don't add to the Search Tab
    };
    
};
