package com.happysg.createbiomechanical.content.cogolem.behavior;

import com.happysg.createbiomechanical.content.cogolem.CogolemEntity;
import com.mojang.datafixers.util.Pair;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.List;

public class ProtectOwner extends ExtendedBehaviour<CogolemEntity> {
    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return List.of();
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, CogolemEntity entity) {
        // Check if the entity has an owner
        LivingEntity owner = entity.getOwner();
        if (owner == null) {
            return false; // No owner to protect
        }
        LivingEntity target = owner.getLastAttacker();
        // Check if the owner has a last attacker and if that attacker is alive
        if (target == null || target.isDeadOrDying()) {
            return false; // No valid target to protect against
        }
        return super.checkExtraStartConditions(level, entity);
    }

    @Override
    protected void start(CogolemEntity entity) {
        LivingEntity owner = entity.getOwner();
        if (owner == null) {
            return; // No owner to protect
        }
        LivingEntity target = owner.getLastAttacker();
        if (target == null || target.isDeadOrDying()) {
            return; // No valid target to protect against
        }
        BrainUtils.setTargetOfEntity(entity, target); // Set the target of the Cogolem to the entity that attacked its owner
        //todo prevent mobs from even trying to attack the owner,
        // scan nearby and verify if entities are targeting owner
        super.start(entity);
    }
}
