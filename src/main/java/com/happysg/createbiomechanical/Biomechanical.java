package com.happysg.createbiomechanical;

import com.happysg.createbiomechanical.registry.*;
import com.happysg.createbiomechanical.content.tuner.GolemTunerOverlayRenderer;
import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.providers.RegistrateDataProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.slf4j.Logger;

@Mod(Biomechanical.MODID)
public class Biomechanical {
    public static final String MODID = "createbiomechanical";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MODID).defaultCreativeTab((ResourceKey<CreativeModeTab>) null);

    public Biomechanical(IEventBus modEventBus, ModContainer modContainer) {
        LOGGER.info("Hello from Create Biomechanical!");
        REGISTRATE.registerEventListeners(modEventBus);
        BMBlocks.register();
        BMBlockEntityTypes.register();
        BMItems.register();
        BMEntityTypes.register();
        BMPartials.register();
        BMCreativeModTabs.register(modEventBus);
        modEventBus.addListener(Biomechanical::gatherData);
        modEventBus.addListener(GolemTunerOverlayRenderer::registerOverlay);
    }

    private static void gatherData(GatherDataEvent event) {
        event.getGenerator().addProvider(true, REGISTRATE.setDataProvider(new RegistrateDataProvider(REGISTRATE, MODID, event)));
    }



    public static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    public static String humanize(String path) {
        String string = path.replaceAll("_", " ").replaceAll("-", " ").toLowerCase();
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }
}
