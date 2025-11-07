package com.happysg.createbiomechanical.content.station;

import com.happysg.createbiomechanical.registry.BMPartials;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.content.kinetics.base.ShaftRenderer;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.liukrast.multipart.block.IMultipartBlock;
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
        int shape = state.getValue(((IMultipartBlock)state.getBlock()).getPartsProperty());
        Direction facing = state.getValue(StationBlock.FACING);
        if (shape == 4) {
            ms.translate(-facing.getStepX(), -facing.getStepY(), -facing.getStepZ());
            VertexConsumer consumer = buffer.getBuffer(RenderType.cutout());
            SuperByteBuffer frame = CachedBuffers.partialFacing(BMPartials.STATION_FRAME, state, facing.getOpposite());
            frame.light(light);
            frame.renderInto(ms, consumer);
            return;
        }


        if (shape != 2 && shape != 8)
            return;

        Direction shaftFacing = shape == 2 ? facing.getClockWise() : facing.getCounterClockWise();
        RenderType type = getRenderType(be, state);
        SuperByteBuffer shaft = CachedBuffers.partialFacing(BMPartials.STATION_SHAFT, state, shaftFacing);

        if (type != null)
            renderRotatingBuffer(be, shaft, ms, buffer.getBuffer(type), light);
    }

}
