package com.petrolpark.petrolsparts.content.pneumatic_tube;

import com.mojang.blaze3d.vertex.PoseStack;
import com.petrolpark.petrolsparts.PetrolsPartsPartialModels;
import com.petrolpark.tube.ITubeRenderer;
import com.petrolpark.util.KineticsHelper;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public class PneumaticTubeRenderer extends KineticBlockEntityRenderer<PneumaticTubeBlockEntity> implements ITubeRenderer<PneumaticTubeBlockEntity> {

    public PneumaticTubeRenderer(Context context) {
        super(context);
    };

    @Override
    protected void renderSafe(PneumaticTubeBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        BlockState state = be.getBlockState();
        renderTube(be, ms, buffer, light);
        //TODO arrows

        //if (VisualizationManager.supportsVisualization(be.getLevel())) return;
		renderRotatingBuffer(be, getRotatedModel(be, state), ms, buffer.getBuffer(getRenderType(be, state)), light);
    };

    @Override
    public PartialModel getTubeSegmentModel(PneumaticTubeBlockEntity be) {
        return PetrolsPartsPartialModels.PNEUMATIC_TUBE_SEGMENT;
    };

    @Override
    protected SuperByteBuffer getRotatedModel(PneumaticTubeBlockEntity be, BlockState state) {
        Direction face = state.getValue(PneumaticTubeBlock.FACING);
        return CachedBuffers.partialDirectional(AllPartialModels.SHAFTLESS_COGWHEEL, state, face, () -> KineticsHelper.rotateToFace(face.getOpposite()));
    };

    @Override
    public boolean shouldRenderOffScreen(PneumaticTubeBlockEntity pBlockEntity) {
        return true;
    };
    
};
