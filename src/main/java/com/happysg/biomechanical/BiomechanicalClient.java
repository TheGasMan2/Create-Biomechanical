package com.happysg.biomechanical;

import net.createmod.ponder.foundation.PonderIndex;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@Mod(value = BiomechanicalConstants.MOD_ID, dist = Dist.CLIENT)
public class BiomechanicalClient {

    public BiomechanicalClient(IEventBus eventBus) {
        eventBus.register(this);
    }

    @SubscribeEvent
    private void fMLClientSetup(FMLClientSetupEvent event) {
        PonderIndex.addPlugin(new BMPonderPlugin());
    }
}
