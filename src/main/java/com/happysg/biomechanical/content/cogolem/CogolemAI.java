package com.happysg.biomechanical.content.cogolem;

import com.happysg.biomechanical.content.cogolem.behavior.FindStation;
import com.happysg.biomechanical.content.cogolem.behavior.FollowOwner;
import com.happysg.biomechanical.content.cogolem.behavior.ProtectOwner;
import com.happysg.biomechanical.registry.BMBlocks;
import com.happysg.biomechanical.world.entity.Cogolem;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.liukrast.multipart.block.IMultipartBlock;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Enemy;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.behaviour.FirstApplicableBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.OneRandomBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.attack.AnimatableMeleeAttack;
import net.tslat.smartbrainlib.api.core.behaviour.custom.look.LookAtTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.Idle;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.MoveToWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetRandomWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetWalkTargetToAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.InvalidateAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetPlayerLookTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetRandomLookTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.TargetOrRetaliate;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.custom.NearbyBlocksSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyLivingEntitySensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyPlayersSensor;

import java.util.List;

public class CogolemAI {

    public static List<? extends ExtendedSensor<Cogolem>> getSensors() {
        return ObjectArrayList.of(
                new NearbyPlayersSensor<>(),
                new NearbyLivingEntitySensor<>(),
                new NearbyBlocksSensor<Cogolem>()
                        .setRadius(64, 2)
                        .setPredicate((blockState, livingEntity) -> blockState.is(BMBlocks.STATION) && blockState.getValue(((IMultipartBlock)blockState.getBlock()).getPartsProperty()) == 4)
                        .setScanRate(e -> (int) Math.max(e.getChargeLevel(), 30))
        );
    }

    public static BrainActivityGroup<Cogolem> getCoreTasks() {
        return BrainActivityGroup.coreTasks(
                new FollowOwner<>().stopFollowingWithin(2).startCondition(cogolemEntity -> cogolemEntity.getCommand() == GolemCommands.FOLLOW && cogolemEntity.getChargeLevel() > 20),
                new FindStation().startCondition(cogolemEntity -> cogolemEntity.getCommand() == GolemCommands.STATION || cogolemEntity.getChargeLevel() < 25),
                new MoveToWalkTarget<Cogolem>().startCondition(cogolemEntity -> cogolemEntity.getCommand() != GolemCommands.STAY && cogolemEntity.getChargeLevel() > 0),
                new LookAtTarget<Cogolem>().runFor(entity -> entity.getRandom().nextIntBetweenInclusive(40, 300)).startCondition(cogolemEntity -> cogolemEntity.getCommand() != GolemCommands.STAY)
        );

    }

    public static BrainActivityGroup<Cogolem> getIdleTasks() {
        return BrainActivityGroup.idleTasks(
                new FirstApplicableBehaviour<Cogolem>(
                        new TargetOrRetaliate<Cogolem>()
                                .attackablePredicate(target -> target instanceof Enemy && !(target instanceof Creeper))
                                .startCondition(golem -> golem.getChargeLevel() > 15 && golem.getCommand() == GolemCommands.WANDER),
                        new ProtectOwner().startCondition(cogolemEntity -> cogolemEntity.getCommand() != GolemCommands.STAY),
                        new SetPlayerLookTarget<Cogolem>()
                                .startCondition(cogolemEntity -> cogolemEntity.getCommand() != GolemCommands.STAY),
                        new SetRandomLookTarget<Cogolem>()
                                .startCondition(cogolemEntity -> cogolemEntity.getCommand() != GolemCommands.STAY)),
                new OneRandomBehaviour<>(
                        new SetRandomWalkTarget<Cogolem>()
                                .startCondition(cogolemEntity -> cogolemEntity.getCommand() == GolemCommands.WANDER),
                        new Idle<>().runFor(entity -> entity.getRandom().nextInt(30, 60)))
        );
    }

    public static BrainActivityGroup<Cogolem> getFightTasks() {
        return BrainActivityGroup.fightTasks(
                new InvalidateAttackTarget<>(),
                new SetWalkTargetToAttackTarget<>(),
                new AnimatableMeleeAttack<Cogolem>(2)
                        .attackInterval(e -> 30)
                        .startCondition(cogolemEntity -> cogolemEntity.getChargeLevel() > 0)
                        .whenStarting(cogolemEntity -> cogolemEntity.takeCharge(5))
        );
    }

}
