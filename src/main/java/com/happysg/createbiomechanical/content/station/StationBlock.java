package com.happysg.createbiomechanical.content.station;

import com.happysg.createbiomechanical.registry.BMBlockEntityTypes;
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
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.NonnullDefault;

import java.util.List;
import java.util.stream.IntStream;

@NonnullDefault
public class StationBlock extends KineticBlock implements IBE<StationBlockEntity>, IMultipartBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    private final List<VoxelShaper> shapes = IntStream.range(0, 10)
            .mapToObj(i -> VoxelShaper.forHorizontal(switch (i) {
                case 0 -> Block.box(10, 0, 0, 16, 4, 16);
                case 1, 7 -> Block.box(0, 0, 12, 16, 16, 16);
                case 2 -> Block.box(10, 4, 3, 14, 12, 16);
                case 3 -> Block.box(10, 4, 0, 14, 12, 16);
                case 4 -> Shapes.empty();
                case 6 -> Block.box(0, 0, 0, 6, 4, 16);
                case 8 -> Block.box(2, 4, 3, 6, 12, 16);
                case 9 -> Block.box(2, 4, 0, 6, 12, 16);
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

    /* MUTLIPART */

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
    public void setPlacedBy(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, LivingEntity placer, @NotNull ItemStack stack) {
        IMultipartBlock.super.setPlacedBy(level, pos, state, placer, stack);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
        IMultipartBlock.super.createBlockStateDefinition$multipart(builder);
        builder.add(FACING);
    }

    @Override
    public boolean canSurvive(@NotNull BlockState state, @NotNull LevelReader level, @NotNull BlockPos pos) {
        return IMultipartBlock.super.canSurvive(state, level, pos);
    }

    @Override
    public void destroy(@NotNull LevelAccessor level, @NotNull BlockPos pos, @NotNull BlockState state) {
        IMultipartBlock.super.destroy(level, pos, state);
    }

    //TODO: UPDATE THIS
    @Override
    protected @NotNull BlockState mirror(@NotNull BlockState state, Mirror mirror) {
        return rotate(state, mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected @NotNull BlockState rotate(BlockState state, Rotation rotation) {
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
        if(shape == 2 || shape == 8)
            return state.getValue(FACING).getCounterClockWise().getAxis();
        return null;
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        int shape = state.getValue(getPartsProperty());
        Direction facing = state.getValue(FACING);
        if (shape == 2) return face == facing.getClockWise();
        if (shape == 8) return face == facing.getCounterClockWise();
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
        if (pState.getValue(getPartsProperty()) == 5) {
            return RenderShape.MODEL;
        }
        return RenderShape.INVISIBLE;
    }

}
