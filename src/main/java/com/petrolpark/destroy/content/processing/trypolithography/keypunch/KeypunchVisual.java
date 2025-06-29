package com.petrolpark.destroy.content.processing.trypolithography.keypunch;

import com.petrolpark.destroy.client.DestroyPartials;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.simpleRelays.encased.EncasedCogVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.TransformedInstance;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual;
import net.createmod.catnip.animation.AnimationTickHolder;

public class KeypunchVisual extends EncasedCogVisual implements SimpleDynamicVisual {

    protected final TransformedInstance piston;

    public KeypunchVisual(VisualizationContext ctx, KeypunchBlockEntity blockEntity, float partialTick) {
        super(ctx, blockEntity, false, partialTick, Models.partial(AllPartialModels.SHAFTLESS_COGWHEEL));
        piston = instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial(DestroyPartials.KEYPUNCH_PISTON))
            .createInstance();

        updateAnimation();
    }

    @Override
    public void beginFrame(Context ctx) {
        updateAnimation();
    }

    @Override
    public void updateLight(float partialTick) {
        super.updateLight(partialTick);
        relight(pos, piston);
    }

    @Override
    public void _delete() {
        super._delete();
        piston.delete();
    }

    private void updateAnimation() {
        KeypunchBlockEntity be = (KeypunchBlockEntity)blockEntity;
        CircuitPunchingBehaviour behaviour = be.punchingBehaviour;
        float renderedHeadOffset = behaviour.getRenderedPistonOffset(AnimationTickHolder.getPartialTicks());

        int pistonPos = be.getActualPosition();

        piston.setIdentityTransform()
            .translate(getVisualPosition())
            .translate((4 + 2 * (pistonPos % 4)) / 16f, - (6.1f + (renderedHeadOffset * 12.5f)) / 16f, (4 + 2 * (pistonPos / 4)) / 16f);
    }
}
