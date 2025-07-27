package com.happysg.createbiomechanical.content.cogolem;

import net.minecraft.world.entity.LivingEntity;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.constant.DefaultAnimations;

public class CogolemAnimation {

    public static final RawAnimation STAND = RawAnimation.begin().thenPlay("stand");

    static PlayState predicate(CogolemEntity cogolem, AnimationState<CogolemEntity> animationState) {
        return PlayState.CONTINUE;
    }

    static void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar, CogolemEntity cogolem) {
        AnimationController<CogolemEntity> controller =
                new AnimationController<>(cogolem, "controller", 0,
                        state -> CogolemAnimation.predicate(cogolem, state));
        controllerRegistrar.add(controller);
        controllerRegistrar.add(DefaultAnimations.genericDeathController(cogolem));
        controllerRegistrar.add(DefaultAnimations.genericWalkIdleController(cogolem));
        controllerRegistrar.add(genericAttackAnimation(cogolem,DefaultAnimations.ATTACK_SWING));
        controllerRegistrar.add(new AnimationController<>(cogolem, "charge", state -> PlayState.CONTINUE)
                .triggerableAnim("sit", DefaultAnimations.SIT).triggerableAnim("stand", STAND));
    }

    public static <T extends LivingEntity & GeoAnimatable> AnimationController<T> genericAttackAnimation(T animatable, RawAnimation attackAnimation) {
        return new AnimationController<>(animatable, "Attack", 10, state -> {
            if (animatable.swinging)
                return state.setAndContinue(attackAnimation);

            state.getController().forceAnimationReset();

            return PlayState.STOP;
        });
    }
}
