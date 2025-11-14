package com.happysg.biomechanical.registry;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.level.block.state.pattern.BlockPatternBuilder;
import net.minecraft.world.level.block.state.predicate.BlockStatePredicate;

import java.util.function.Predicate;

public class BMBlockPatternMatch {
    public static final Predicate<BlockState> PUMPKIN_PREDICATE = state -> state != null && (state.is(Blocks.CARVED_PUMPKIN) || state.is(Blocks.JACK_O_LANTERN));

    private static BlockPattern COGOLEM = null;
    public static BlockPattern getOrCreateCogolemFull() {
        if(COGOLEM == null) {
            COGOLEM = BlockPatternBuilder.start()
                    .aisle("^", "#", "$")
                    .where('^', BlockInWorld.hasState(PUMPKIN_PREDICATE))
                    .where('#', BlockInWorld.hasState(BlockStatePredicate.forBlock(BMBlocks.POWER_CORE.get())))
                    .where('$', BlockInWorld.hasState(BlockStatePredicate.forBlock(Blocks.TUFF)))
                    .build();
        }
        return COGOLEM;
    }
}
