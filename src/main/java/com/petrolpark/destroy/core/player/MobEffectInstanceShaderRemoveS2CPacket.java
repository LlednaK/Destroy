package com.petrolpark.destroy.core.player;

import com.petrolpark.destroy.util.IGameRendererMixin;
import com.petrolpark.destroy.util.IMobEffectInstanceMixin;
import com.petrolpark.network.packet.S2CPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class MobEffectInstanceShaderRemoveS2CPacket extends S2CPacket {
    ResourceLocation mobEffect;

    public MobEffectInstanceShaderRemoveS2CPacket(MobEffect mobEffect) {
        this.mobEffect = ForgeRegistries.MOB_EFFECTS.getKey(mobEffect);
    }

    public MobEffectInstanceShaderRemoveS2CPacket(FriendlyByteBuf buffer) {
        this.mobEffect = buffer.readResourceLocation();
    }

    @Override
    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeResourceLocation(this.mobEffect);
    }

    @Override
    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() -> {
            LocalPlayer player = Minecraft.getInstance().player;
            IGameRendererMixin gameRenderer = (( IGameRendererMixin ) Minecraft.getInstance().gameRenderer);
            if (player != null) {
                MobEffect effect = ForgeRegistries.MOB_EFFECTS.getValue(mobEffect);
                if (effect != null) {
                    MobEffectInstance instance = player.getEffect(effect);
                    if (instance != null) {
                        gameRenderer.removeMobEffectInstanceShader((( IMobEffectInstanceMixin ) instance));
                    }
                }
            }
        });
        return true;
    }
}
