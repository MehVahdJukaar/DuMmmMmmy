package net.mehvahdjukaar.dummmmmmy.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.mehvahdjukaar.dummmmmmy.configs.ClientConfigs;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.object.banner.BannerFlagModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BannerRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.util.Mth;

public class LayerDummyCape extends RenderLayer<TargetDummyRenderState, TargetDummyModel> {

    private final BannerFlagModel flag;
    private final SpriteGetter sprites;

    public LayerDummyCape(RenderLayerParent<TargetDummyRenderState, TargetDummyModel> renderer,
                          EntityRendererProvider.Context context) {
        super(renderer);
        this.flag = new BannerFlagModel(context.bakeLayer(ModelLayers.STANDING_BANNER_FLAG));
        this.sprites = context.getSprites();
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector collector, int lightCoords,
                       TargetDummyRenderState state, float yRot, float xRot) {
        if (state.bannerColor == null || state.isInvisibleToPlayer) return;

        poseStack.pushPose();
        float scale = 0.5f;
        var head = getParentModel().getHead();
        poseStack.translate(head.x / 16.0F, head.y / 16.0F, head.z / 16.0F);
        poseStack.translate(0, 0, 2 / 16f);

        float bodyXRot = getParentModel().getBody().xRot;

        float capeRest = 0.08f;

        float swingAmount = Math.min((float) (state.swing * ClientConfigs.ANIMATION_INTENSITY.get()), 40f);
        float capeSwingAngle = capeRest + (1 - capeRest - Mth.sin(state.shake)) * 0.02f * swingAmount;

        float naturalSwayPhase = (state.ageInTicks % 100.0F) / 100.0F;
        float naturalSway = 0.005F * Mth.cos(Mth.TWO_PI * naturalSwayPhase) * Mth.PI;
        capeSwingAngle += naturalSway;

        poseStack.mulPose(Axis.XP.rotation((bodyXRot + Mth.PI + Math.max(0, capeSwingAngle))));
        poseStack.scale(-scale, -scale, 1);
        poseStack.translate(0, 0, state.chestOpenForCape ? 1 / 16f : 0);

        BannerRenderer.submitPatterns(this.sprites, poseStack, collector, lightCoords, OverlayTexture.NO_OVERLAY,
                this.flag, 0.0F, true, state.bannerColor, state.bannerPatterns, null);

        poseStack.popPose();
    }

}
