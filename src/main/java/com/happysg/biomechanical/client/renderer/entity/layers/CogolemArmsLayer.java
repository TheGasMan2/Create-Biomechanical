package com.happysg.biomechanical.client.renderer.entity.layers;

import com.happysg.biomechanical.BiomechanicalConstants;
import com.happysg.biomechanical.world.entity.Cogolem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.joml.Vector3f;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;
import software.bernie.geckolib.util.RenderUtil;

public class CogolemArmsLayer extends GeoRenderLayer<Cogolem> {

    private final GeoModel<Cogolem> model = new DefaultedEntityGeoModel<>(BiomechanicalConstants.id("oak")) {
        @Override
        protected String subtype() {
            return super.subtype() + "/cogolem/arms";
        }
    };

    public CogolemArmsLayer(GeoRenderer<Cogolem> parent) {
        super(parent);
    }

    @Override
    public GeoModel<Cogolem> getGeoModel() {
        return model;
    }

    //TODO: Currently doesn't support arm-animations
    @Override
    public void renderForBone(PoseStack poseStack, Cogolem animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        if(!bone.getName().equals("left_arm") && !bone.getName().equals("right_arm")) return;
        boolean left = bone.getName().equals("left_arm");
        poseStack.pushPose();
        var optArmToRender = getDefaultBakedModel(animatable).getBone((left?"left":"right") + "_arm");
        if(optArmToRender.isEmpty()) return;
        var armToRender = optArmToRender.get();
        var color = renderer.getRenderColor(animatable, partialTick, packedLight).argbInt();
        renderType = RenderType.entityCutout(model.getTextureResource(animatable, renderer));
        var vertex = bufferSource.getBuffer(renderType);
        renderer.renderCubesOfBone(
                poseStack,
                armToRender,
                vertex,
                packedLight, packedOverlay,
                color
        );
        renderer.renderChildBones(
                poseStack,
                animatable,
                armToRender,
                renderType, bufferSource, vertex,
                false, partialTick, packedLight, packedOverlay, color);
        poseStack.popPose();
    }
}
