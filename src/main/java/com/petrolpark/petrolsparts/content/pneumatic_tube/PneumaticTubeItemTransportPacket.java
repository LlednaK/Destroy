package com.petrolpark.petrolsparts.content.pneumatic_tube;

import java.util.function.Supplier;

import com.petrolpark.network.packet.S2CPacket;
import com.petrolpark.petrolsparts.PetrolsPartsBlockEntityTypes;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent.Context;

public class PneumaticTubeItemTransportPacket extends S2CPacket {

    public final BlockPos pneumaticTubePos;
    public final ItemStack stack;

    public PneumaticTubeItemTransportPacket(BlockPos pneumaticTubePos, ItemStack stack) {
        this.pneumaticTubePos = pneumaticTubePos;
        this.stack = stack;
    };

    public PneumaticTubeItemTransportPacket(FriendlyByteBuf buffer) {
        pneumaticTubePos = buffer.readBlockPos();
        stack = buffer.readItem();
    };

    @Override
    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pneumaticTubePos);
        buffer.writeItem(stack);
    };

    @Override
    public boolean handle(Supplier<Context> supplier) {
        supplier.get().enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            mc.level.getBlockEntity(pneumaticTubePos, PetrolsPartsBlockEntityTypes.PNEUMATIC_TUBE.get())
                .flatMap(PneumaticTubeBlockEntity::asInput)
                .ifPresent(input -> input.transport(stack));
        });
        return true;
    };
    
};
