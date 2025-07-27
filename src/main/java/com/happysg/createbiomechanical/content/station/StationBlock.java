package com.happysg.createbiomechanical.content.station;

import com.happysg.createbiomechanical.registry.BMBlockEntityTypes;
import com.happysg.createbiomechanical.registry.BMBlocks;
import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class StationBlock extends HorizontalKineticBlock implements IBE<StationBlockEntity> {

    public static final EnumProperty<StationShape> SHAPE = EnumProperty.create("shape", StationShape.class);

    public StationBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(SHAPE, StationShape.CENTER));
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (level.getBlockEntity(pos) instanceof StationBlockEntity stationBlockEntity) {
            stationBlockEntity.getController().ifPresent(controller -> controller.onEntityInside(entity));
        }
    }

    @Override
    public void onPlace(BlockState state, Level worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, worldIn, pos, oldState, isMoving);
        if (state.getValue(SHAPE) != StationShape.CENTER) {
            return;
        }
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
        //todo better way to do this, define and move these offsets to StationShape
        worldIn.setBlockAndUpdate(leftShaftPos, defaultBlockState().setValue(SHAPE, StationShape.TOP_FRONT_LEFT_SHAFT).setValue(HORIZONTAL_FACING, front));
        withBlockEntityDo(worldIn, leftShaftPos, be -> be.setControllerPos(pos));
        worldIn.setBlockAndUpdate(rightShaftPos, defaultBlockState().setValue(SHAPE, StationShape.TOP_FRONT_RIGHT_SHAFT).setValue(HORIZONTAL_FACING, front));
        withBlockEntityDo(worldIn, rightShaftPos, be -> be.setControllerPos(pos));
        worldIn.setBlockAndUpdate(frontLeftPos, defaultBlockState().setValue(SHAPE, StationShape.BOTTOM_FRONT_LEFT).setValue(HORIZONTAL_FACING, front));
        withBlockEntityDo(worldIn, frontLeftPos, be -> be.setControllerPos(pos));
        worldIn.setBlockAndUpdate(frontCenterPos, defaultBlockState().setValue(SHAPE, StationShape.FRONT_CENTER).setValue(HORIZONTAL_FACING, front));
        withBlockEntityDo(worldIn, frontCenterPos, be -> be.setControllerPos(pos));
        worldIn.setBlockAndUpdate(frontRightPos, defaultBlockState().setValue(SHAPE, StationShape.BOTTOM_FRONT_RIGHT).setValue(HORIZONTAL_FACING, front));
        withBlockEntityDo(worldIn, frontRightPos, be -> be.setControllerPos(pos));
        worldIn.setBlockAndUpdate(backLeftPos, defaultBlockState().setValue(SHAPE, StationShape.BOTTOM_BACK_LEFT).setValue(HORIZONTAL_FACING, front));
        withBlockEntityDo(worldIn, backLeftPos, be -> be.setControllerPos(pos));
        worldIn.setBlockAndUpdate(backRightPos, defaultBlockState().setValue(SHAPE, StationShape.BOTTOM_BACK_RIGHT).setValue(HORIZONTAL_FACING, front));
        withBlockEntityDo(worldIn, backRightPos, be -> be.setControllerPos(pos));
        worldIn.setBlockAndUpdate(topBackLeftPos, defaultBlockState().setValue(SHAPE, StationShape.TOP_BACK_LEFT).setValue(HORIZONTAL_FACING, front));
        withBlockEntityDo(worldIn, topBackLeftPos, be -> be.setControllerPos(pos));
        worldIn.setBlockAndUpdate(topBackRightPos, defaultBlockState().setValue(SHAPE, StationShape.TOP_BACK_RIGHT).setValue(HORIZONTAL_FACING, front));
        withBlockEntityDo(worldIn, topBackRightPos, be -> be.setControllerPos(pos));

    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (pState.getValue(SHAPE) != StationShape.CENTER) {
            withBlockEntityDo(pLevel, pPos, StationBlockEntity::removeMultiBlock);
            super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
            return;
        }
        Direction front = pState.getValue(HORIZONTAL_FACING);
        Direction left = pState.getValue(HORIZONTAL_FACING).getClockWise();
        Direction right = pState.getValue(HORIZONTAL_FACING).getCounterClockWise();
        //todo better way to do this
        BlockPos leftShaftPos = pPos.relative(front).above().relative(left);
        BlockPos rightShaftPos = pPos.relative(front).above().relative(right);
        BlockPos frontLeftPos = pPos.relative(front).relative(left);
        BlockPos frontCenterPos = pPos.relative(front);
        BlockPos frontRightPos = pPos.relative(front).relative(right);
        BlockPos backLeftPos = pPos.relative(left);
        BlockPos backRightPos = pPos.relative(right);
        BlockPos topBackLeftPos = pPos.above().relative(left);
        BlockPos topBackRightPos = pPos.above().relative(right);

        destroyStationBlock(pLevel, leftShaftPos, rightShaftPos, frontLeftPos, frontCenterPos,
                frontRightPos, backLeftPos, backRightPos, topBackLeftPos, topBackRightPos);

        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);

    }

    private static void destroyStationBlock(Level pLevel, BlockPos... pPos) {
        for (BlockPos pos : pPos) {
            if (pLevel.getBlockState(pos).is(BMBlocks.STATION.get())) {
                pLevel.destroyBlock(pos, false);
            }
        }
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        StationShape shape = state.getValue(SHAPE);
        if (shape == StationShape.TOP_FRONT_LEFT_SHAFT || shape == StationShape.TOP_FRONT_RIGHT_SHAFT) {
            return state.getValue(HORIZONTAL_FACING).getCounterClockWise().getAxis();
        }
        return null;
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        StationShape shape = state.getValue(SHAPE);
        Direction front = state.getValue(HORIZONTAL_FACING);
        if (shape == StationShape.TOP_FRONT_LEFT_SHAFT) {
            return face == front.getClockWise();
        }
        if (shape == StationShape.TOP_FRONT_RIGHT_SHAFT) {
            return face == front.getCounterClockWise();
        }
        return false;
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return pState.getValue(SHAPE).getShape(pState.getValue(HORIZONTAL_FACING));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(SHAPE);
        super.createBlockStateDefinition(builder);
    }

    @Override
    public Class<StationBlockEntity> getBlockEntityClass() {
        return StationBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends StationBlockEntity> getBlockEntityType() {
        return BMBlockEntityTypes.STATION.get();
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        if (pState.getValue(SHAPE) == StationShape.CENTER) {
            return RenderShape.MODEL;
        }
        return RenderShape.INVISIBLE;
    }

}
