package com.happysg.createbiomechanical.content.station;


import net.createmod.catnip.lang.Lang;
import net.createmod.catnip.math.VoxelShaper;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

//for the station block, we need to define the shapes of the station multi block parts
//two of them are used for kinetic shaft input logic as well
public enum StationShape implements StringRepresentable {

    CENTER(Block.box(0, 0, 0, 16, 16, 16)),
    BOTTOM_BACK_LEFT(Block.box(0, 0, 12, 16, 16, 16)),
    BOTTOM_BACK_RIGHT(Block.box(0, 0, 12, 16, 16, 16)),
    TOP_BACK_LEFT(Block.box(10, 4, 0, 14, 12, 16)),
    TOP_BACK_RIGHT(Block.box(2, 4, 0, 6, 12, 16)),
    TOP_FRONT_LEFT_SHAFT(Block.box(10, 4, 3, 14, 12, 16)),
    TOP_FRONT_RIGHT_SHAFT(Block.box(2, 4, 3, 6, 12, 16)),
    BOTTOM_FRONT_LEFT(Block.box(10, 0, 0, 16, 4, 16)),
    FRONT_CENTER(Shapes.empty()),
    BOTTOM_FRONT_RIGHT(Block.box(0, 0, 0, 6, 4, 16));

    private final VoxelShaper shaper;

    StationShape(VoxelShape northShape) {
        this.shaper = VoxelShaper.forHorizontal(northShape, Direction.NORTH);
    }

    public VoxelShape getShape(Direction facing) {
        return shaper.get(facing);
    }

    @Override
    public String getSerializedName() {
        return Lang.asId(name());
    }
}

