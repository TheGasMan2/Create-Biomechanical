package com.happysg.createbiomechanical.registry;

import com.happysg.createbiomechanical.Biomechanical;
import com.happysg.createbiomechanical.content.station.StationBlockEntity;
import com.happysg.createbiomechanical.content.station.StationBlockEntityRenderer;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

import static com.happysg.createbiomechanical.Biomechanical.REGISTRATE;

public class BMBlockEntityTypes {
    public static final BlockEntityEntry<StationBlockEntity> STATION = REGISTRATE
            .blockEntity("station", StationBlockEntity::new)
            .renderer(() -> StationBlockEntityRenderer::new)
            .validBlocks(BMBlocks.STATION)
            .register();

    public static void register() {
        Biomechanical.LOGGER.info("Registering Block Entity Types for " + Biomechanical.MODID);
    }
}