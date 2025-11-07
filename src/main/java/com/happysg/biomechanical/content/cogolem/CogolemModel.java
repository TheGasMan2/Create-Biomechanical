package com.happysg.biomechanical.content.cogolem;

import com.happysg.biomechanical.BiomechanicalConstants;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class CogolemModel extends DefaultedEntityGeoModel<CogolemEntity> {
    @Override
    public void handleAnimations(CogolemEntity animatable, long instanceId, AnimationState<CogolemEntity> animationState, float partialTick) {
        super.handleAnimations(animatable, instanceId, animationState, partialTick);
    }

    public CogolemModel() {
        super(BiomechanicalConstants.id("cogolem"), true);
    }
}
