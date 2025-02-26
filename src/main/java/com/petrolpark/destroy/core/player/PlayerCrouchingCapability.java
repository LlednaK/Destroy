package com.petrolpark.destroy.core.player;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

public class PlayerCrouchingCapability {

    public static final Capability<PlayerCrouchingCapability> CAPABILITY = CapabilityManager.get(new CapabilityToken<PlayerCrouchingCapability>() {});

    public int ticksCrouching; // How long the Player has been crouching
    public int ticksUrinating; // How long the Player has been urinating

    public static class Provider implements ICapabilityProvider, INBTSerializable<CompoundTag> {

        private PlayerCrouchingCapability playerCrouching = null;
        private final LazyOptional<PlayerCrouchingCapability> optional = LazyOptional.of(this::createPlayerCrouching);

        private PlayerCrouchingCapability createPlayerCrouching() {
            if (playerCrouching == null) {
                playerCrouching = new PlayerCrouchingCapability();
            };
            return playerCrouching;
        };

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag tag = new CompoundTag();
            PlayerCrouchingCapability playerCrouching = createPlayerCrouching();
            tag.putInt("Crouching", playerCrouching.ticksCrouching);
            tag.putInt("Urinating", playerCrouching.ticksUrinating);
            return tag;
        };

        @Override
        public void deserializeNBT(CompoundTag tag) {
            PlayerCrouchingCapability playerCrouching = createPlayerCrouching();
            playerCrouching.ticksCrouching = tag.getInt("Crouching");
            playerCrouching.ticksUrinating = tag.getInt("Urinating");
        };

        @Override
        public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
            if(cap == CAPABILITY) {
                return optional.cast();
            };
            return LazyOptional.empty();
        };

    };
};
