package com.petrolpark.destroy.content.oil.seismology;

import java.util.function.Consumer;

import com.simibubi.create.foundation.item.render.SimpleCustomRenderer;

import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

public class SeismometerItem extends Item {

    public SeismometerItem(Properties properties) {
        super(properties);
    };

    @Override
    @OnlyIn(Dist.CLIENT)
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		consumer.accept(SimpleCustomRenderer.create(this, new SeismometerItemRenderer()));
	};
    
};
