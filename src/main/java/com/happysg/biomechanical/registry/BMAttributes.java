package com.happysg.biomechanical.registry;

import com.happysg.biomechanical.BiomechanicalConstants;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class BMAttributes {
    private static final DeferredRegister<Attribute> REGISTER = BiomechanicalConstants.deferred(BuiltInRegistries.ATTRIBUTE);
    private BMAttributes() {}

    public static final Holder<Attribute> MAX_CHARGE = REGISTER.register("max_charge", () -> new RangedAttribute("attribute.name.biomechanical.max_charge", 200, 0, Double.MAX_VALUE));

    public static void init(IEventBus eventBus) {
        REGISTER.register(eventBus);
    }
}
