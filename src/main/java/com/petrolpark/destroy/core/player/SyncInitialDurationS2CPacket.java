package com.petrolpark.destroy.core.player;

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

public class SyncInitialDurationS2CPacket extends S2CPacket {
    int initialDuration;
    ResourceLocation mobEffectRL;

    public SyncInitialDurationS2CPacket(int initialDuration, MobEffect mobEffect) {
        this.initialDuration = initialDuration;
        this.mobEffectRL = ForgeRegistries.MOB_EFFECTS.getKey(mobEffect);
    }

    public SyncInitialDurationS2CPacket(FriendlyByteBuf buffer) {
        this.initialDuration = buffer.readInt();
        this.mobEffectRL = buffer.readResourceLocation();
    }

    @Override
    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeInt(initialDuration);
        buffer.writeResourceLocation(mobEffectRL);
    }

    @Override
    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() -> {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null) {
                MobEffect effect = ForgeRegistries.MOB_EFFECTS.getValue(mobEffectRL);
                if (effect != null) {
                    MobEffectInstance instance = player.getEffect(effect);
                    if (instance != null) {
                        (( IMobEffectInstanceMixin ) instance).setInitialDuration(initialDuration);
                    }
                }
            }
        });
        return true;
    }
}
