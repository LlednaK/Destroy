package com.petrolpark.petrolsparts.content.pneumatic_tube;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Queue;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.petrolpark.core.item.QueueItemHandler;
import com.petrolpark.petrolsparts.PetrolsPartsBlockEntityTypes;
import com.petrolpark.tube.ITubeBlockEntity;
import com.petrolpark.tube.TubeBehaviour;
import com.petrolpark.util.BlockFace;
import com.petrolpark.util.MathsHelper;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.content.logistics.box.PackageEntity;
import com.simibubi.create.content.logistics.funnel.FunnelFilterSlotPositioning;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.CapManipulationBehaviourBase;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.InvManipulationBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.VersionedInventoryTrackerBehaviour;
import com.simibubi.create.foundation.item.ItemHelper.ExtractionCountMode;

import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.animation.LerpedFloat.Chaser;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class PneumaticTubeBlockEntity extends KineticBlockEntity implements ITubeBlockEntity {

    public final TubeBehaviour tube;

    protected Optional<Either<Input, Output>> handler = Optional.empty();

    protected FilteringBehaviour filtering;
    protected InvManipulationBehaviour targetInventory;
    protected VersionedInventoryTrackerBehaviour invVersionTracker;
    protected DirectBeltInputBehaviour beltInput;

    protected final QueueItemHandler itemBacklog = new QueueItemHandler();

    public PneumaticTubeBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
        tube = new TubeBehaviour(this);
    };

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);

        // Both ends
        behaviours.add(tube);
        behaviours.add(beltInput = new DirectBeltInputBehaviour(this));
        behaviours.add(targetInventory = new InvManipulationBehaviour(this, CapManipulationBehaviourBase.InterfaceProvider.oppositeOfBlockFacing()));
        
        // Relevant to input end only
        behaviours.add(invVersionTracker = new VersionedInventoryTrackerBehaviour(this));
        filtering = new FilteringBehaviour(this, new FunnelFilterSlotPositioning()) //TODO replace slot positioning
		    .showCountWhen(this::supportsAmountOnFilter) 
		    .onlyActiveWhen(this::isInput)
		    .withCallback(this::onFilterChanged);
		behaviours.add(filtering);
    };

    public Optional<Either<Input, Output>> getHandler() {
        return handler;
    };

    protected Optional<PneumaticTubeBlockEntity> getOther() {
        if (tube.getSpline() == null) return Optional.empty();
        return getLevel().getBlockEntity(tube.getOtherEndPos(), PetrolsPartsBlockEntityTypes.PNEUMATIC_TUBE.get());
    };

    protected Optional<Either<Input, Output>> getOtherHandler() {
        return getOther().flatMap(PneumaticTubeBlockEntity::getHandler);
    };

    public boolean isInput() {
        return getHandler().<Input>flatMap(Either::left).isPresent();
    };

    public boolean isOutput() {
        return getHandler().<Output>flatMap(Either::right).isPresent();
    };

    protected Optional<Input> getInput() {
        return getHandler().<Input>flatMap(Either::left).or(() -> getOtherHandler().<Input>flatMap(Either::left));
    };

    protected Optional<Output> getOutput() {
        return getHandler().<Output>flatMap(Either::right).or(() -> getOtherHandler().<Output>flatMap(Either::right));
    };

    public QueueItemHandler getItemBacklog() {
        return itemBacklog;
    };

    protected float getCombinedAbsSpeed() {
        return Math.abs(getSpeed()) + getOther().map(PneumaticTubeBlockEntity::getSpeed).map(Math::abs).orElse(0f);
    };

    public void onFilterChanged(ItemStack filterStack) {
        resetInvVersionTracker();
        getOther().ifPresent(PneumaticTubeBlockEntity::resetInvVersionTracker);
    };

    public boolean supportsAmountOnFilter() {
        return true; //TODO check this works for Belts
    };

    public void resetInvVersionTracker() {
        invVersionTracker.reset();
    };

    public class Output {

        protected Boolean blocked = null;
        protected WeakReference<Entity> lastBlockingEntityRef = null;

        public Optional<Input> getInput() {
            return getOtherHandler().flatMap(Either::left);
        };

        public Direction getOutputFace() {
            return getBlockState().getValue(PneumaticTubeBlock.FACING).getOpposite();
        };

        public Vec3 getOutputLocation() {
            final Direction outputFace = getOutputFace();
            return BlockFace.of(getBlockPos(), outputFace).getCenter().relative(outputFace, 1 / 32d);
        };

        public void forgetBlocked() {
            blocked = null;
        };

        public boolean isBlocked() {
            if (blocked == null) {
                final BlockPos outputPos = getBlockPos().relative(getOutputFace());
                final BlockHitResult blockHit = level.clip(new ClipContext(getOutputLocation(), Vec3.atCenterOf(outputPos), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, null));
                blocked = (blockHit.getBlockPos().equals(outputPos));
            };
            return blocked;
        };

        /**
         * Attempt to insert the Item Stack into the space in front of this output.
         * If the Stack cannot be outputted and this is not simulated, stores the Stack in the {@link PneumaticTubeBlockEntity#itemBacklog backlog}.
         * @param stack
         * @param simulate Whether to not actually store/shoot out the Item, but just try
         * @return The number of remaining Items after the Insertion is/would be complete
         */
        protected int output(ItemStack stack, boolean simulate) {
            final Direction outputFace = getOutputFace();
            final BlockPos outputPos = getBlockPos().relative(outputFace);

            // First: check for Belts
            //TODO

            // Second: check for Inventories
            if (simulate) targetInventory.simulate();
            ItemStack remainder = targetInventory.insert(stack);
            if (!remainder.equals(stack, false)) return remainder.getCount();
            if (targetInventory.hasInventory()) return backlog(stack, simulate); // Don't try shooting out the item if the Inventory exists but is just full

            // Third: check to try shoot out Items
            final float speed = getCombinedAbsSpeed();
            if (isBlocked() || speed == 0f) return backlog(stack, simulate);
            boolean blockingEntityPresent = true;
            final AABB blockingArea = new AABB(outputPos);
            if (lastBlockingEntityRef == null) {
                blockingEntityPresent = false;
            } else {
                Entity lastBlockingEntity = lastBlockingEntityRef.get();
                if (lastBlockingEntity == null || !lastBlockingEntity.isAlive() || !lastBlockingEntity.getBoundingBox().intersects(blockingArea)) {
				    blockingEntityPresent = false;
				    lastBlockingEntity = null;
			    };
            };
            if (blockingEntityPresent) return backlog(stack, simulate);
            // Find a new Blocking Entity
            for (Entity entity : level.getEntities(null, blockingArea)) {
                if (entity instanceof ItemEntity || entity instanceof PackageEntity) {
                    lastBlockingEntityRef = new WeakReference<>(entity);
                    return backlog(stack, simulate);
                };
            };
            if (!simulate) {
                final Vec3 outputLoc = getOutputLocation();
                final ItemEntity itemEntity = new ItemEntity(level, outputLoc.x(), outputLoc.y(), outputLoc.z(), stack);
                itemEntity.setDefaultPickUpDelay();
                itemEntity.setDeltaMovement(new Vec3(outputFace.step()).scale(Math.pow(speed, 0.25f)));
                level.addFreshEntity(itemEntity);
                lastBlockingEntityRef = new WeakReference<>(itemEntity);
            };
            return 0;
        };

        public int backlog(ItemStack stack, boolean simulate) {
            final int amount = stack.getCount();
            if (!simulate) itemBacklog.add(stack);
            return amount;
        };
    };

    public class Input {

        public Queue<StackTransporting> stacksTransporting;

        public Optional<Output> getOutput() {
            return getOtherHandler().flatMap(Either::right);
        };

        public Collection<StackTransporting> getStacksTransporting() {
            return stacksTransporting;
        };

        public void input() {

            if (!getOther()
                .filter(PneumaticTubeBlockEntity::isOutput)
                .map(PneumaticTubeBlockEntity::getItemBacklog)
                .map(QueueItemHandler::isEmpty)
                .orElse(false)
            ) return; // No Item transport possible if the Output is missing or backlogged

            if (invVersionTracker.stillWaiting(targetInventory)) return; // Don't transport if there is an attached Inventory but it still doesn't fit the Filtering requirements

            ItemStack toTryTransport = targetInventory.simulate().extract(getModeToExtract(), getAmountToExtract(), filtering::test);
            //TODO also try extract from Belt

            if (toTryTransport.isEmpty()) return;
            final int remaining = getOutput().map(output -> output.output(toTryTransport, true)).orElse(toTryTransport.getCount());
            if (toTryTransport.getCount() <= remaining) return; // Don't transport if it won't fit in the output

            final ItemStack toTransport = targetInventory.extract(ExtractionCountMode.EXACTLY, toTryTransport.getCount() - remaining, filtering::test);
            //TODO also extract from belt
            //TODO begin transport
        };

        public ExtractionCountMode getModeToExtract() {
            if (!supportsAmountOnFilter() || !filtering.isActive()) return ExtractionCountMode.UPTO;
            return targetInventory.getModeFromFilter();
        };

        public int getAmountToExtract() {
            if (!supportsAmountOnFilter()) return 64;
            if (!filtering.isActive()) return 1;
            return targetInventory.getAmountFromFilter();
        };

        public void tick() {

        };
    };

    @Override
    public void onSpeedChanged(float previousSpeed) {
        super.onSpeedChanged(previousSpeed);
        getInput().map(Input::getStacksTransporting).ifPresent(stacks -> stacks.forEach(StackTransporting::updateAnimation));
    };

    public final Codec<StackTransporting> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ItemStack.CODEC.fieldOf("item").forGetter(StackTransporting::getStack),
        Codec.INT.fieldOf("progress").forGetter(StackTransporting::getProgress)
    ).apply(instance, StackTransporting::new));

    public class StackTransporting {

        protected final ItemStack stack;
        protected int progress;
        public final LerpedFloat animation = LerpedFloat.linear().chase(1f, 0f, Chaser.LINEAR).startWithValue(0f);

        public StackTransporting(ItemStack stack, int progress) {
            this.stack = stack;
            this.progress = progress;
            updateAnimation();
        };

        public ItemStack getStack() {
            return stack;
        };

        public int getProgress() {
            return progress;
        };

        public void updateAnimation() {
            animation.updateChaseSpeed(preventSpeedUpdate); //TODO
        };

        public void tick() {
            progress++; //TODO depend on speed
            animation.tickChaser();
        };
    };

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        itemBacklog.deserializeNBT(compound.getList("Backlog", Tag.TAG_COMPOUND));
    };

    @Override
    protected void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        compound.put("Backlog", itemBacklog.serializeNBT());
    };
    
    @Override
    public void invalidateTubeRenderBoundingBox() {
        invalidateRenderBoundingBox();
    };

    @Override
    protected AABB createRenderBoundingBox() {
        if (!tube.isController() || tube.getSpline() == null) return super.createRenderBoundingBox();
        return MathsHelper.expandToInclude(tube.getSpline().getOccupiedVolume(), getBlockPos());
    };
    
};
