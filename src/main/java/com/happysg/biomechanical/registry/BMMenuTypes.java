package com.happysg.biomechanical.registry;

import com.happysg.biomechanical.BiomechanicalConstants;
import com.happysg.biomechanical.client.gui.screens.inventory.CogolemScreen;
import com.happysg.biomechanical.world.inventory.CogolemMenu;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class BMMenuTypes {
    private BMMenuTypes() {}

    private static final DeferredRegister<MenuType<?>> REGISTER = BiomechanicalConstants.deferred(BuiltInRegistries.MENU);

    public static final DeferredHolder<MenuType<?>, MenuType<CogolemMenu>> COGOLEM_MENU = REGISTER.register("cogolem", () -> IMenuTypeExtension.create(CogolemMenu::new));

    public static void init(IEventBus eventBus) {
        REGISTER.register(eventBus);
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerMenuScreens(RegisterMenuScreensEvent event) {
        event.register(COGOLEM_MENU.get(), CogolemScreen::new);
    }
}
