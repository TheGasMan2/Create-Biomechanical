package com.happysg.createbiomechanical.content.station;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import static com.simibubi.create.content.kinetics.base.HorizontalKineticBlock.HORIZONTAL_FACING;

public class StationBlockItem extends BlockItem {

    public StationBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    protected boolean canPlace(BlockPlaceContext pContext, BlockState state) {
        BlockPos pos = pContext.getClickedPos();
        Direction front = state.getValue(HORIZONTAL_FACING);
        Direction left = state.getValue(HORIZONTAL_FACING).getClockWise();
        Direction right = state.getValue(HORIZONTAL_FACING).getCounterClockWise();
        BlockPos leftShaftPos = pos.relative(front).above().relative(left);
        BlockPos rightShaftPos = pos.relative(front).above().relative(right);
        BlockPos frontLeftPos = pos.relative(front).relative(left);
        BlockPos frontCenterPos = pos.relative(front);
        BlockPos frontRightPos = pos.relative(front).relative(right);
        BlockPos backLeftPos = pos.relative(left);
        BlockPos backRightPos = pos.relative(right);
        BlockPos topBackLeftPos = pos.above().relative(left);
        BlockPos topBackRightPos = pos.above().relative(right);
        // TODO better system needed
        boolean replaceable = checkReplaceable(pContext.getLevel(), leftShaftPos, rightShaftPos,
                frontLeftPos, frontCenterPos, frontRightPos, backLeftPos, backRightPos, topBackLeftPos, topBackRightPos);

        if (!replaceable) {
            Player player = pContext.getPlayer();
            if (player != null && player.isLocalPlayer()) {
                player.sendSystemMessage(Component.literal("Cannot place station here, some blocks are not replaceable."));
            }
        }

        return super.canPlace(pContext, state) && replaceable;
    }

    private boolean checkReplaceable(Level level, BlockPos... pos) {
        for (BlockPos blockPos : pos) {
            BlockState state = level.getBlockState(blockPos);
            if (!state.isAir() && !state.canBeReplaced()) {
                return false;
            }
        }
        return true;
    }
}
