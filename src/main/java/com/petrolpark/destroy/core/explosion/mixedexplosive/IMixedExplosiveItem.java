package com.petrolpark.destroy.core.explosion.mixedexplosive;

import com.mojang.datafixers.util.Either;
import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.core.explosion.mixedexplosive.ExplosiveProperties.ExplosivePropertyCondition;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(value = Dist.CLIENT, modid = Destroy.MOD_ID, bus = EventBusSubscriber.Bus.FORGE)
public interface IMixedExplosiveItem {
    
    public default MixedExplosiveInventory getExplosiveInventory(ItemStack stack) {
        if (!stack.getItem().equals(this)) return new MixedExplosiveInventory(getExplosiveInventorySize(), getApplicableExplosionConditions());
        MixedExplosiveInventory inventory = new MixedExplosiveInventory(getExplosiveInventorySize(), getApplicableExplosionConditions());
        inventory.deserializeNBT(stack.getOrCreateTag().getCompound("ExplosiveMix"));
        return inventory;
    };

    public default void setExplosiveInventory(ItemStack stack, MixedExplosiveInventory inv) {
        if (inv != null && stack.getItem().equals(this)) stack.getOrCreateTag().put("ExplosiveMix", inv.serializeNBT());
    };

    public int getExplosiveInventorySize();

    public ExplosivePropertyCondition[] getApplicableExplosionConditions();

    @SubscribeEvent
    public static void onGatherTooltips(RenderTooltipEvent.GatherComponents event) {
        Minecraft mc = Minecraft.getInstance();
        ExplosiveProperties properties = null;
        if (event.getItemStack().getItem() instanceof IMixedExplosiveItem mixItem) {
            properties = mixItem.getExplosiveInventory(event.getItemStack()).getExplosiveProperties().withConditions(mixItem.getApplicableExplosionConditions());
        } else if (mc.screen instanceof MixedExplosiveScreen) {
            properties = ExplosiveProperties.ITEM_EXPLOSIVE_PROPERTIES.get(event.getItemStack().getItem());
        };
        if (properties != null) event.getTooltipElements().add(Either.right(new ExplosivePropertiesTooltip(properties)));
    };
};
