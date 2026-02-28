package com.happysg.biomechanical.content.cogolem.behavior;

import com.google.common.collect.ImmutableMap;
import com.happysg.biomechanical.registry.BMMemoryModuleTypes;
import com.happysg.biomechanical.world.entity.Cogolem;
import com.happysg.biomechanical.registry.BMBlocks;
import com.mojang.datafixers.util.Pair;
import net.liukrast.multipart.block.IMultipartBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.level.block.state.BlockState;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.registry.SBLMemoryTypes;

import java.util.List;
import java.util.Optional;

public class FindStation extends Behavior<Cogolem> {
    private static final int TICKS_UNTIL_TIMEOUT = 1200;
    final float speedModifier;

    public FindStation(float speedModifier) {
        super(ImmutableMap.of(BMMemoryModuleTypes.COGOLEM_STATIONS.get(), MemoryStatus.VALUE_PRESENT), TICKS_UNTIL_TIMEOUT);
        this.speedModifier = speedModifier;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, Cogolem owner) {
        return owner.getBrain()
                .getActiveNonCoreActivity()
                .map(act -> act == Activity.IDLE || act == Activity.WORK || act == Activity.PLAY)
                .orElse(true);
    }

    @Override
    protected boolean canStillUse(ServerLevel level, Cogolem entity, long gameTime) {
        return entity.getBrain().hasMemoryValue(BMMemoryModuleTypes.COGOLEM_STATIONS.get());
    }

    @Override
    protected void tick(ServerLevel level, Cogolem owner, long gameTime) {
        BehaviorUtils.setWalkAndLookTargetMemories(
                owner, owner.getBrain().getMemory(BMMemoryModuleTypes.COGOLEM_STATIONS.get()).get().pos(), this.speedModifier, 1
        );
    }

    @Override
    protected void stop(ServerLevel level, Cogolem entity, long gameTime) {
        Optional<GlobalPos> optional = entity.getBrain().getMemory(BMMemoryModuleTypes.COGOLEM_STATIONS.get());
        optional.ifPresent(globalPos -> {
            BlockPos blockPos = globalPos.pos();
            ServerLevel serverLevel = level.getServer().getLevel(globalPos.dimension());
            if(serverLevel == null) return;
            PoiManager poiManager = serverLevel.getPoiManager();
            if(poiManager.exists(blockPos, type -> true))
                poiManager.release(blockPos);
            DebugPackets.sendPoiTicketCountPacket(level, blockPos);
        });
        entity.getBrain().eraseMemory(BMMemoryModuleTypes.COGOLEM_STATIONS.get());
    }
}
