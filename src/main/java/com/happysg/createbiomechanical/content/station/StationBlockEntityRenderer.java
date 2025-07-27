package com.happysg.createbiomechanical.content.station;

import com.happysg.createbiomechanical.registry.BMPartials;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.content.kinetics.base.ShaftRenderer;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public class StationBlockEntityRenderer extends ShaftRenderer<StationBlockEntity> {

    public StationBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(StationBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        BlockState state = be.getBlockState();
        StationShape shape = state.getValue(StationBlock.SHAPE);
        Direction facing = state.getValue(StationBlock.HORIZONTAL_FACING);
        if (shape == StationShape.FRONT_CENTER) {
            ms.translate(-facing.getStepX(), -facing.getStepY(), -facing.getStepZ());
            VertexConsumer consumer = buffer.getBuffer(RenderType.cutout());
            SuperByteBuffer frame = CachedBuffers.partialFacing(BMPartials.STATION_FRAME, state, facing.getOpposite());
            frame.light(light);
            frame.renderInto(ms, consumer);
            return;
        }


        if (shape != StationShape.TOP_FRONT_LEFT_SHAFT && shape != StationShape.TOP_FRONT_RIGHT_SHAFT)
            return;

        Direction shaftFacing = shape == StationShape.TOP_FRONT_LEFT_SHAFT ? facing.getClockWise() : facing.getCounterClockWise();
        RenderType type = getRenderType(be, state);
        SuperByteBuffer shaft = CachedBuffers.partialFacing(BMPartials.STATION_SHAFT, state, shaftFacing);

        if (type != null)
            renderRotatingBuffer(be, shaft, ms, buffer.getBuffer(type), light);
    }

}
