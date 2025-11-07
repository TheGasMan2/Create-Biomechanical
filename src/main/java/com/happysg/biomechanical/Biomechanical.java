package com.happysg.biomechanical;

import com.happysg.biomechanical.registry.*;
import com.happysg.biomechanical.content.tuner.GolemTunerOverlayRenderer;
import com.simibubi.create.foundation.data.CreateRegistrate;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(BiomechanicalConstants.MOD_ID)
public class Biomechanical {
    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(BiomechanicalConstants.MOD_ID)
            .defaultCreativeTab((ResourceKey<CreativeModeTab>) null); //???

    public Biomechanical(IEventBus modEventBus, ModContainer modContainer) {
        REGISTRATE.registerEventListeners(modEventBus);
        /* INIT REGISTRIES */
        BMBlocks.register();
        BMBlockEntityTypes.register();
        BMItems.register();
        BMEntityTypes.register();
        BMPartials.register();
        BMCreativeModTabs.register(modEventBus);

        /* EVENTS */
        modEventBus.addListener(GolemTunerOverlayRenderer::registerOverlay);
    }

    @Deprecated(forRemoval = true)
    public static String humanize(String path) {
        String string = path.replaceAll("_", " ").replaceAll("-", " ").toLowerCase();
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }
}
