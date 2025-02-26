package com.petrolpark.destroy.core.event;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.DestroyClient;
import com.petrolpark.destroy.client.DestroyLang;
import com.petrolpark.destroy.content.oil.seismology.SeismometerItemRenderer;
import com.petrolpark.destroy.content.tool.swissarmyknife.SwissArmyKnifeItem;
import com.petrolpark.destroy.core.block.entity.BlockEntityBehaviourRenderer;

import net.minecraft.ChatFormatting;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(value = Dist.CLIENT, modid = Destroy.MOD_ID)
public class DestroyClientEvents {

    /**
     * Tick a couple of renderers.
     * @param event
     */
    @SubscribeEvent
    public static final void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            SeismometerItemRenderer.tick();
            SwissArmyKnifeItem.clientPlayerTick();
            DestroyClient.EXTENDED_INVENTORY_HANDLER.tick(event);
        } else {
            BlockEntityBehaviourRenderer.tick();
        };
    };

    /**
     * Add a bit of pedantry to the TNT tooltip.
     * @param event
     */
    @SubscribeEvent
    public static final void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        Item item = stack.getItem();

        if (item.equals(Items.TNT)) event.getToolTip().add(DestroyLang.translate("tooltip.tnt").style(ChatFormatting.GRAY).component());
    };
};
