package com.happysg.biomechanical.content.cogolem;

import com.happysg.biomechanical.BiomechanicalConstants;
import com.happysg.biomechanical.world.entity.Cogolem;
import software.bernie.geckolib.model.DefaultedGeoModel;

public class TestModel extends DefaultedGeoModel<Cogolem> {
    public TestModel() {
        super(BiomechanicalConstants.id("default_hands"));
    }

    @Override
    protected String subtype() {
        return "arm";
    }
}
