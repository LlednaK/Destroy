package com.petrolpark.destroy.mixin;

import com.simibubi.create.content.contraptions.MountedFluidStorage;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.foundation.fluid.SmartFluidTank;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.petrolpark.destroy.core.fluid.GeniusFluidTankBehaviour.GeniusFluidTank;

@Mixin(MountedFluidStorage.class)
public abstract class MountedFluidStorageMixin {
    @Inject(
        method = "Lcom/simibubi/create/content/contraptions/MountedFluidStorage;createMountedTank(Lnet/minecraft/world/level/block/entity/BlockEntity;)Lcom/simibubi/create/foundation/fluid/SmartFluidTank;",
        at = @At("HEAD"),
        cancellable = true,
        remap = false
    )
    private void createGeniusMountedTank(BlockEntity be, CallbackInfoReturnable<SmartFluidTank> cir) {
        if(be instanceof FluidTankBlockEntity tank) {
            cir.setReturnValue(new GeniusFluidTank(
                tank.getTotalTankSize() * FluidTankBlockEntity.getCapacityMultiplier(),
                this::invokeOnFluidStackChanged));
        }
    }

    @Invoker(
        value = "onFluidStackChanged",
        remap = false
    )
    public abstract void invokeOnFluidStackChanged(FluidStack stack);
}
