package com.happysg.biomechanical.registry;

import com.happysg.biomechanical.world.level.block.entity.StationBlockEntity;
import com.happysg.biomechanical.client.renderer.blockentity.StationBlockEntityRenderer;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

import static com.happysg.biomechanical.Biomechanical.REGISTRATE;

public class BMBlockEntityTypes {
    public static final BlockEntityEntry<StationBlockEntity> STATION = REGISTRATE
            .blockEntity("station", StationBlockEntity::new)
            .renderer(() -> StationBlockEntityRenderer::new)
            .validBlocks(BMBlocks.STATION)
            .register();

    public static void register() {}
}