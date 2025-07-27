package com.happysg.createbiomechanical.content.cogolem;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class CogolemRenderer extends GeoEntityRenderer<CogolemEntity> {

    // Define the textures as static final constants using ResourceLocation.fromNamespaceAndPath
    private static final ResourceLocation DEFAULT_TEXTURE = ResourceLocation.fromNamespaceAndPath("createbiomechanical", "textures/entity/cogolem.png");
    private static final ResourceLocation ANDESITE_ALLOY_TEXTURE = ResourceLocation.fromNamespaceAndPath("createbiomechanical", "textures/entity/cogolem_andesite_alloy.png");
    private static final ResourceLocation BRASS_TEXTURE = ResourceLocation.fromNamespaceAndPath("createbiomechanical", "textures/entity/cogolem_brass.png");

    // Constructor for rendering
    public CogolemRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new CogolemModel());
    }

    @Override
    public ResourceLocation getTextureLocation(CogolemEntity animatable) {
        switch (animatable.getTextureState()) {
            case 1:
                return ANDESITE_ALLOY_TEXTURE;
            case 2:
                return BRASS_TEXTURE;
            default:
                return DEFAULT_TEXTURE;
        }
    }

    @Override
    protected float getDeathMaxRotation(CogolemEntity animatable) {
        return 0.0F;
    }
}
