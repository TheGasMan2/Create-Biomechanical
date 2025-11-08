package com.happysg.biomechanical.registry;

import com.happysg.biomechanical.BiomechanicalConstants;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.happysg.biomechanical.Biomechanical.REGISTRATE;

public class BMCreativeModTabs {

    private static final DeferredRegister<CreativeModeTab> REGISTER =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, BiomechanicalConstants.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> CREATE_BIOMECHANICAL_TAB = REGISTER.register("main",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(BMBlocks.COGOLEM_HEAD))
                    .title(Component.translatable("itemGroup.biomechanical.main"))
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
        REGISTER.register(eventBus);
        REGISTRATE.addRawLang("itemGroup.biomechanical.main", "Create: Biomechanical");
    }
}

