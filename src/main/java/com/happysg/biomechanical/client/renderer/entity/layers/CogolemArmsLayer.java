package com.happysg.biomechanical.client.renderer.entity.layers;

import com.happysg.biomechanical.BiomechanicalConstants;
import com.happysg.biomechanical.world.entity.Cogolem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class CogolemArmsLayer extends GeoRenderLayer<Cogolem> {
    //TODO: DYNAMIC REGISTRABLE ARMS?
    private static final ResourceLocation TEXTURE = BiomechanicalConstants.png("textures/entity/cogolem/arms/oak");

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

    @Override
    public void render(PoseStack poseStack, Cogolem animatable, BakedGeoModel bakedModel, @Nullable RenderType renderType, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        renderType = RenderType.entityCutout(TEXTURE);
        BakedGeoModel armsModel = getDefaultBakedModel(animatable);
        var body = getProperties("body", bakedModel);
        var chest = body == null ? null : body.add(getProperties("chest", bakedModel));
        var rightArm = chest == null ? null : chest.add(getProperties("right_arm", bakedModel));
        var leftArm = chest == null ? null : chest.add(getProperties("left_arm", bakedModel));
        if(rightArm == null || leftArm == null) return;
        armsModel.getBone("right_arm").ifPresent(ra -> set(ra, rightArm));
        armsModel.getBone("left_arm").ifPresent(la -> set(la, leftArm));
        getRenderer().reRender(armsModel, poseStack, bufferSource, animatable, renderType,
                bufferSource.getBuffer(renderType), partialTick, LightTexture.FULL_SKY, packedOverlay,
                getRenderer().getRenderColor(animatable, partialTick, packedLight).argbInt());
    }
    
    private Properties getProperties(String name, BakedGeoModel model) {
        var opt = model.getBone(name);
        if(opt.isEmpty()) {
            BiomechanicalConstants.LOGGER.error("Cannot find part {} in model", name);
            return null;
        }
        var m = opt.get();
        return new Properties(m.getPosX(), m.getPosY(), m.getPosZ(), m.getRotX(), m.getRotY(), m.getRotZ(), m.getScaleX(), m.getScaleY(), m.getScaleZ(), m.isHidden());
    }
    
    private record Properties(float x, float y, float z, float rX, float rY, float rZ, float sX, float sY, float sZ, boolean hidden) {
        public Properties add(Properties o) {
            if(o == null) return null;
            return new Properties(this.x + o.x, this.y + o.y, this.z + o.z, this.rX + o.rX, this.rY + o.rY, this.rZ + o.rZ, this.sX * o.sX, this.sY * o.sY, this.sZ * o.sZ, this.hidden && o.hidden);
        }
    }

    private static void set(GeoBone bone, Properties p) {
        bone.setPosX(p.x);
        bone.setPosY(p.y);
        bone.setPosZ(p.z);
        bone.setRotX(p.rX);
        bone.setRotY(p.rY);
        bone.setRotZ(p.rZ);
        bone.setScaleX(p.sX);
        bone.setScaleY(p.sY);
        bone.setScaleZ(p.sZ);
        bone.setHidden(p.hidden);
    }
}
