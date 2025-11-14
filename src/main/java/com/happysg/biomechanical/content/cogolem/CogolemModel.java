package com.happysg.biomechanical.content.cogolem;

import com.happysg.biomechanical.BiomechanicalConstants;
import com.happysg.biomechanical.world.entity.Cogolem;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class CogolemModel extends DefaultedEntityGeoModel<Cogolem> {
    @Override
    public void handleAnimations(Cogolem animatable, long instanceId, AnimationState<Cogolem> animationState, float partialTick) {
        super.handleAnimations(animatable, instanceId, animationState, partialTick);
    }

    public CogolemModel() {
        super(BiomechanicalConstants.id("cogolem"), true);
    }
}
