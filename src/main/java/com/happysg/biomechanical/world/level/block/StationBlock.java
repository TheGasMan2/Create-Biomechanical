package com.happysg.biomechanical.world.level.block;

import com.happysg.biomechanical.world.level.block.entity.StationBlockEntity;
import com.happysg.biomechanical.registry.BMBlockEntityTypes;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.createmod.catnip.math.VoxelShaper;
import net.liukrast.multipart.block.AbstractMultipartBlock;
import net.liukrast.multipart.block.IMultipartBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.NonnullDefault;

import java.util.List;
import java.util.stream.IntStream;

@NonnullDefault
public class StationBlock extends KineticBlock implements IBE<StationBlockEntity>, IMultipartBlock {
    public static final int BOTTOM_FRONT_LEFT = 0;
    public static final int BOTTOM_BACK_LEFT = 1;
    public static final int TOP_FRONT_LEFT = 2;
    public static final int TOP_BACK_LEFT = 3;
    public static final int CENTER = 4;
    public static final int BOTTOM_FRONT_RIGHT = 5;
    public static final int BOTTOM_BACK_RIGHT = 6;
    public static final int TOP_FRONT_RIGHT = 7;
    public static final int TOP_BACK_RIGHT = 8;

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    private final List<VoxelShaper> shapes = IntStream.range(0, 10)
            .mapToObj(i -> VoxelShaper.forHorizontal(switch (i) {
                case 0 -> Block.box(10, 0, 0, 16, 4, 16);
                case 1, 6 -> Block.box(0, 0, 12, 16, 16, 16);
                case 2 -> Block.box(10, 4, 3, 14, 12, 16);
                case 3 -> Block.box(10, 4, 0, 14, 12, 16);
                case 5 -> Block.box(0, 0, 0, 6, 4, 16);
                case 7 -> Block.box(2, 4, 3, 6, 12, 16);
                case 8 -> Block.box(2, 4, 0, 6, 12, 16);
                default -> Block.box(0, 0, 0, 16, 16, 16);
            }, Direction.NORTH)).toList();
    @Nullable
    private List<BlockPos> positions;
    @Nullable
    private IntegerProperty property;

    public StationBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(getPartsProperty(), 4));
    }

    @Override
    public void defineParts(AbstractMultipartBlock.Builder builder) {
        for(int x = 0; x < 3; x++)
            for(int y = 0; y < 2; y++)
                for(int z = 0; z < 2; z++)
                    if(x != 1 || y != 1)
                        if(x != 1 || z != 0)
                            builder.define(x,y,z);
    }

    /* MULTIPART IMPLEMENTATION */

    @Override
    public Direction getDirection(BlockState state) {
        return state.getValue(FACING);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public @Nullable List<BlockPos> getPositions() {
        return positions;
    }

    @Override
    public void setPositions(List<BlockPos> positions) {
        this.positions = positions;
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public IntegerProperty getPartsProperty() {
        return property;
    }

    @Override
    public void setPartsProperty(IntegerProperty property) {
        this.property = property;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        IMultipartBlock.super.setPlacedBy(level, pos, state, placer, stack);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
        IMultipartBlock.super.createBlockStateDefinition$multipart(builder);
        builder.add(FACING);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return IMultipartBlock.super.canSurvive(state, level, pos);
    }

    @Override
    public void destroy(LevelAccessor level, BlockPos pos, BlockState state) {
        IMultipartBlock.super.destroy(level, pos, state);
    }

    //TODO: UPDATE THIS
    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return rotate(state, mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (level.getBlockEntity(pos) instanceof StationBlockEntity stationBlockEntity) {
            stationBlockEntity.getController().ifPresent(controller -> controller.onEntityInside(entity));
        }
    }

    @Nullable
    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        int shape = state.getValue(getPartsProperty());
        if(shape == TOP_FRONT_LEFT || shape == TOP_FRONT_RIGHT)
            return state.getValue(FACING).getCounterClockWise().getAxis();
        return null;
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        int shape = state.getValue(getPartsProperty());
        Direction facing = state.getValue(FACING);
        if (shape == TOP_FRONT_LEFT) return face == facing.getClockWise();
        if (shape == TOP_FRONT_RIGHT) return face == facing.getCounterClockWise();
        return false;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return shapes.get(state.getValue(getPartsProperty())).get(state.getValue(FACING));
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
        if (pState.getValue(getPartsProperty()) == CENTER) {
            return RenderShape.MODEL;
        }
        return RenderShape.INVISIBLE;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return state.getValue(getPartsProperty()) == CENTER ? IBE.super.newBlockEntity(pos, state) : null;
    }
}
