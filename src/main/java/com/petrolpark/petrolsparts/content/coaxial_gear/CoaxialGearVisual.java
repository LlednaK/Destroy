package com.petrolpark.petrolsparts.content.coaxial_gear;

import java.util.function.Consumer;

import com.petrolpark.petrolsparts.PetrolsPartsPartialModels;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.content.kinetics.base.RotatingInstance;
import com.simibubi.create.content.kinetics.base.SingleAxisRotatingVisual;
import com.simibubi.create.content.kinetics.simpleRelays.ICogWheel;
import com.simibubi.create.foundation.render.AllInstanceTypes;

import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.model.Model;
import dev.engine_room.flywheel.api.visual.BlockEntityVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.model.Models;
import net.minecraft.core.Direction;

import com.simibubi.create.content.kinetics.simpleRelays.BracketedKineticBlockEntityRenderer;
import com.simibubi.create.content.kinetics.simpleRelays.BracketedKineticBlockEntityVisual;

public class CoaxialGearVisual {
    
    public static final BlockEntityVisual<CoaxialGearBlockEntity> create(VisualizationContext context, CoaxialGearBlockEntity blockEntity, float partialTick) {
        if (ICogWheel.isLargeCog(blockEntity.getBlockState())) {
			return new LargeCoaxialGearVisual(context, blockEntity, partialTick, Models.partial(PetrolsPartsPartialModels.LARGE_COAXIAL_GEAR));
		} else {
			return new SingleAxisRotatingVisual<>(context, blockEntity, partialTick, Models.partial(PetrolsPartsPartialModels.COAXIAL_GEAR));
		}
    };

    /**
     * Copied from {@link BracketedKineticBlockEntityVisual.LargeCogVisual} because thats not extensible.
     */
    public static class LargeCoaxialGearVisual extends SingleAxisRotatingVisual<CoaxialGearBlockEntity> {
    
        protected final RotatingInstance additionalShaft;

        public LargeCoaxialGearVisual(VisualizationContext context, CoaxialGearBlockEntity blockEntity, float partialTick, Model model) {
            super(context, blockEntity, partialTick, model);

            Direction.Axis axis = KineticBlockEntityRenderer.getRotationAxisOf(blockEntity);

			additionalShaft = instancerProvider().instancer(AllInstanceTypes.ROTATING, Models.partial(AllPartialModels.COGWHEEL_SHAFT))
				.createInstance();

			additionalShaft.rotateToFace(axis)
				.setup(blockEntity)
				.setRotationOffset(BracketedKineticBlockEntityRenderer.getShaftAngleOffset(axis, pos))
				.setPosition(getVisualPosition())
				.setChanged();
        };

        @Override
		public void update(float pt) {
			super.update(pt);
			additionalShaft.setup(blockEntity)
				.setRotationOffset(BracketedKineticBlockEntityRenderer.getShaftAngleOffset(rotationAxis(), pos))
				.setChanged();
		};

		@Override
		public void updateLight(float partialTick) {
			super.updateLight(partialTick);
			relight(additionalShaft);
		};

		@Override
		protected void _delete() {
			super._delete();
			additionalShaft.delete();
		};

		@Override
		public void collectCrumblingInstances(Consumer<Instance> consumer) {
			super.collectCrumblingInstances(consumer);
			consumer.accept(additionalShaft);
		};
        
    };
};
