package com.happysg.biomechanical.registry;

import com.happysg.biomechanical.BiomechanicalConstants;
import com.mojang.serialization.Codec;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Optional;

public class BMMemoryModuleTypes {
    private static final DeferredRegister<MemoryModuleType<?>> REGISTER = DeferredRegister.create(BuiltInRegistries.MEMORY_MODULE_TYPE, BiomechanicalConstants.MOD_ID);
    private BMMemoryModuleTypes() {}

    public static final DeferredHolder<MemoryModuleType<?>, MemoryModuleType<GlobalPos>> COGOLEM_STATIONS = REGISTER.register("cogolem_stations", () -> new MemoryModuleType<>(Optional.of(GlobalPos.CODEC)));


    public static void init(IEventBus eventBus) {
        REGISTER.register(eventBus);
    }
}
