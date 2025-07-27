package com.happysg.createbiomechanical.registry;

import com.happysg.createbiomechanical.Biomechanical;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.happysg.createbiomechanical.Biomechanical.REGISTRATE;


public class BMCreativeModTabs {


    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Biomechanical.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> CREATE_BIOMECHANICAL_TAB = CREATIVE_MODE_TABS.register("create_biomechanical_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(BMBlocks.COGOLEM_HEAD))
                    .title(Component.translatable(Biomechanical.MODID + ".creativetab.create_biomechanical_tab"))
                    .displayItems((pParameters, pOutput) -> {
                        pOutput.accept(BMBlocks.STATION);
                        pOutput.accept(BMBlocks.POWER_CORE);
                        pOutput.accept(BMBlocks.BIONIC_CASING);
                        pOutput.accept(BMBlocks.BRASS_PROJECTOR);
                        pOutput.accept(BMBlocks.ANDESITE_PROJECTOR);
                        pOutput.accept(BMItems.INCOMPLETE_ELECTRON_BATTERY);
                        pOutput.accept(BMItems.ELECTRON_BATTERY);
                        pOutput.accept(BMItems.TUNER);
                    })
                    .build());


    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
        REGISTRATE.addRawLang(Biomechanical.MODID + ".creativetab.create_biomechanical_tab", "Create: Biomechanical");
    }
}

