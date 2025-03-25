package com.petrolpark.petrolsparts;

import com.petrolpark.petrolsparts.core.ponder.PetrolsPartsPonderPlugin;

import net.createmod.ponder.foundation.PonderIndex;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class PetrolsPartsClient {

    public static void clientInit(final FMLClientSetupEvent event) {
        PonderIndex.addPlugin(new PetrolsPartsPonderPlugin());
    };

    public static final void clientCtor(IEventBus forgeEventBus, IEventBus modEventBus) {
        PetrolsPartsPartialModels.init();
    };
};
