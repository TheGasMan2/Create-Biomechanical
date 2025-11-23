package com.happysg.biomechanical;

import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BiomechanicalConstants {
    private BiomechanicalConstants() {}

    public static final String MOD_ID = "biomechanical";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static ResourceBuilder pathBuilder(String path, Object... args) {
        return (path1, args1) -> id(String.format(path, args) + "/" + path1, args1);
    }

    public static ResourceLocation png(String path, Object... args) {
        return id(path + ".png", args);
    }

    public static ResourceLocation id(String path, Object... args) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, String.format(path, args));
    }

    @FunctionalInterface
    public interface ResourceBuilder {
        ResourceLocation build(String path, Object... args);
    }
}
