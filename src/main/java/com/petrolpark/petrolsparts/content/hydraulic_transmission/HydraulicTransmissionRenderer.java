package com.petrolpark.petrolsparts.content.hydraulic_transmission;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.petrolpark.petrolsparts.PetrolsPartsPartials;
import com.petrolpark.tube.ITubeRenderer;
import com.petrolpark.util.KineticsHelper;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;

public class HydraulicTransmissionRenderer extends KineticBlockEntityRenderer<HydraulicTransmissionBlockEntity> implements ITubeRenderer<HydraulicTransmissionBlockEntity> {

    public HydraulicTransmissionRenderer(Context context) {
        super(context);
    };

    @Override
    protected void renderSafe(HydraulicTransmissionBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        super.renderSafe(be, partialTicks, ms, buffer, light, overlay);
        Direction facing = be.getBlockState().getValue(HydraulicTransmissionBlock.FACING);
        VertexConsumer vc = buffer.getBuffer(RenderType.solid());
        float time = AnimationTickHolder.getRenderTime();

        renderTube(be, ms, buffer, light);

        ms.pushPose();
        TransformStack.of(ms)
            .center()
            .rotateToFace(facing.getOpposite())
            .multiply(com.mojang.math.Axis.XN.rotationDegrees(-90))
            .uncenter();

        CachedBuffers.partial(PetrolsPartsPartials.HYDRAULIC_TRANSMISSION_PISTON, be.getBlockState())
            .translateZ(Mth.sin(((time * be.getSpeed() * 3f / 5) % 360) * Mth.PI / 180f) * 3 / 32f)
            .light(light)
            .renderInto(ms, vc);
        CachedBuffers.partial(PetrolsPartsPartials.HYDRAULIC_TRANSMISSION_PISTON, be.getBlockState())
            .center()
            .rotateYDegrees(90f)
            .uncenter()
            .translateZ(Mth.cos(((time * be.getSpeed() * 3f / 5) % 360) * Mth.PI / 180f) * 3 / 32f)
            .light(light)
            .renderInto(ms, vc);
        
        ms.popPose();
    };

    @Override
    public PartialModel getTubeSegmentModel(HydraulicTransmissionBlockEntity be) {
        return PetrolsPartsPartials.HYDRAULIC_TRANSMISSION_SEGMENT;
    };

    @Override
    protected SuperByteBuffer getRotatedModel(HydraulicTransmissionBlockEntity be, BlockState state) {
        Direction face = state.getValue(HydraulicTransmissionBlock.FACING);
        return CachedBuffers.partialDirectional(PetrolsPartsPartials.HYDRAULIC_TRANSMISSION_INNER, state, face, () -> KineticsHelper.rotateToFace(face.getOpposite()));
    };

    @Override
    public boolean shouldRenderOffScreen(HydraulicTransmissionBlockEntity pBlockEntity) {
        return true;
    };
    
};
