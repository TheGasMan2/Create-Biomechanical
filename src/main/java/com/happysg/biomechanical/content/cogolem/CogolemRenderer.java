package com.happysg.biomechanical.content.cogolem;

import com.happysg.biomechanical.BiomechanicalConstants;
import com.happysg.biomechanical.world.entity.Cogolem;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class CogolemRenderer extends GeoEntityRenderer<Cogolem> {

    private static final ResourceLocation DEFAULT_TEXTURE = BiomechanicalConstants.png("textures/entity/cogolem");
    private static final ResourceLocation ANDESITE_ALLOY_TEXTURE = BiomechanicalConstants.png("textures/entity/cogolem_andesite_alloy");
    private static final ResourceLocation BRASS_TEXTURE = BiomechanicalConstants.png("textures/entity/cogolem_brass");

    public CogolemRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new CogolemModel());
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(Cogolem animatable) {
        return switch (animatable.getTextureState()) {
            case 1 -> ANDESITE_ALLOY_TEXTURE;
            case 2 -> BRASS_TEXTURE;
            default -> DEFAULT_TEXTURE;
        };
    }

    @Override
    protected float getDeathMaxRotation(Cogolem animatable) {
        return 0.0F;
    }
}
