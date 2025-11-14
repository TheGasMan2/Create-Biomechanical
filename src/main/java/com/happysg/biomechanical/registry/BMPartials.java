package com.happysg.biomechanical.registry;

import com.happysg.biomechanical.BiomechanicalConstants;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;

public class BMPartials {

    public static final PartialModel STATION_FRAME = block("station/station_frame");
    public static final PartialModel STATION_SHAFT = block("station/station_shaft");
    public static final PartialModel GEAR = item("gear");

    private static PartialModel block(String path) {
        return PartialModel.of(BiomechanicalConstants.id("block/" + path));
    }

    private static PartialModel item(String path) {
        return PartialModel.of(BiomechanicalConstants.id("item/" + path));
    }

    private static PartialModel entity(String path) {
        return PartialModel.of(BiomechanicalConstants.id("entity/" + path));
    }

    public static void register() {

    }

}
