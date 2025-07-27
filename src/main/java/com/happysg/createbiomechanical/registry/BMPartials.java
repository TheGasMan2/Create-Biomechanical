package com.happysg.createbiomechanical.registry;

import com.happysg.createbiomechanical.Biomechanical;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;

public class BMPartials {

    public static final PartialModel STATION_FRAME = block("station/station_frame");
    public static final PartialModel STATION_SHAFT = block("station/station_shaft");
    public static final PartialModel GEAR = item("gear");

    private static PartialModel block(String path) {
        return PartialModel.of(Biomechanical.rl("block/" + path));
    }

    private static PartialModel item(String path) {
        return PartialModel.of(Biomechanical.rl("item/" + path));
    }

    private static PartialModel entity(String path) {
        return PartialModel.of(Biomechanical.rl("entity/" + path));
    }

    public static void register() {
        Biomechanical.LOGGER.info("Registering Partials for " + Biomechanical.MODID);
    }

}
