package com.happysg.biomechanical.client.renderer.entity;

import com.happysg.biomechanical.BiomechanicalConstants;
import com.happysg.biomechanical.client.renderer.entity.layers.CogolemArmsLayer;
import com.happysg.biomechanical.world.entity.Cogolem;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class CogolemRenderer extends GeoEntityRenderer<Cogolem> {
    private static final BiomechanicalConstants.ResourceBuilder BUILDER = BiomechanicalConstants.pathBuilder("textures/entity/cogolem");
    private static final ResourceLocation ANDESITE = BUILDER.build("andesite.png");
    private static final ResourceLocation ALLOYED = BUILDER.build("alloyed.png");
    private static final ResourceLocation BRASS = BUILDER.build("brass.png");

    public CogolemRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new DefaultedEntityGeoModel<>(BiomechanicalConstants.id("cogolem"), true));
        addRenderLayer(new CogolemArmsLayer(this));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(Cogolem animatable) {
        return switch (animatable.getVariant()) {
            case ALLOYED -> ALLOYED;
            case BRASS -> BRASS;
            case ANDESITE -> ANDESITE;
        };
    }

    @Override
    protected float getDeathMaxRotation(Cogolem animatable) {
        return 0.0F;
    }
}
