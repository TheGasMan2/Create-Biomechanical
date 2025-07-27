package com.happysg.createbiomechanical.content.cogolem;

import com.happysg.createbiomechanical.Biomechanical;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class CogolemModel extends DefaultedEntityGeoModel<CogolemEntity> {
    @Override
    public void handleAnimations(CogolemEntity animatable, long instanceId, AnimationState<CogolemEntity> animationState, float partialTick) {
        super.handleAnimations(animatable, instanceId, animationState, partialTick);
    }

    public CogolemModel() {
        super(Biomechanical.rl("cogolem"), true);
    }
}
