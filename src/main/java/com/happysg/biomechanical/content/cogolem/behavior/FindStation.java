package com.happysg.biomechanical.content.cogolem.behavior;

import com.happysg.biomechanical.world.entity.Cogolem;
import com.happysg.biomechanical.registry.BMBlocks;
import com.mojang.datafixers.util.Pair;
import net.liukrast.multipart.block.IMultipartBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.level.block.state.BlockState;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.registry.SBLMemoryTypes;

import java.util.List;

public class FindStation extends ExtendedBehaviour<Cogolem> {
    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return List.of(Pair.of(SBLMemoryTypes.NEARBY_BLOCKS.get(), MemoryStatus.VALUE_PRESENT));
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, Cogolem entity) {
        WalkTarget station = entity.getBrain().getMemory(MemoryModuleType.WALK_TARGET).orElse(null);
        if (station == null)
            return true;

        BlockPos pos = station.getTarget().currentBlockPosition();
        return !level.getBlockState(pos).is(BMBlocks.STATION);
    }

    @Override
    protected void start(Cogolem entity) {
        List<Pair<BlockPos, BlockState>> stations = entity.getBrain().getMemory(SBLMemoryTypes.NEARBY_BLOCKS.get()).get();
        BlockPos stationPos = null;
        double closestDistance = Double.MAX_VALUE;
        for (Pair<BlockPos, BlockState> station : stations) {
            if (station.getSecond().is(BMBlocks.STATION) && station.getSecond().getValue(((IMultipartBlock)station.getSecond().getBlock()).getPartsProperty()) == 4) {
                double distance = entity.distanceToSqr(station.getFirst().getCenter());
                if (distance < closestDistance) {
                    stationPos = station.getFirst();
                    closestDistance = distance;
                }
            }
        }
        if (stationPos == null)
            return;
        WalkTarget target = new WalkTarget(stationPos.getCenter(), 1f, 0);
        entity.getBrain().setMemory(MemoryModuleType.WALK_TARGET, target);
    }

}
