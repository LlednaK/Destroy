package com.petrolpark.petrolsparts.content.pneumatic_tube;

import com.mojang.blaze3d.vertex.PoseStack;
import com.petrolpark.petrolsparts.PetrolsPartsPartialModels;
import com.petrolpark.tube.ITubeRenderer;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;

public class PneumaticTubeRenderer extends KineticBlockEntityRenderer<PneumaticTubeBlockEntity> implements ITubeRenderer<PneumaticTubeBlockEntity> {

    public PneumaticTubeRenderer(Context context) {
        super(context);
    };

    @Override
    protected void renderSafe(PneumaticTubeBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        super.renderSafe(be, partialTicks, ms, buffer, light, overlay);
        renderTube(be, ms, buffer, light);
    };

    @Override
    public PartialModel getTubeSegmentModel(PneumaticTubeBlockEntity be) {
        return PetrolsPartsPartialModels.PNEUMATIC_TUBE_SEGMENT;
    };
    
};
