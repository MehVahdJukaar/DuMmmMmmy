package net.mehvahdjukaar.dummmmmmy.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.mehvahdjukaar.dummmmmmy.common.Configs;
import net.mehvahdjukaar.dummmmmmy.entity.DummyNumberEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.text.DecimalFormat;

public class NumberRenderer extends EntityRenderer<DummyNumberEntity> {
    private static final DecimalFormat df = new DecimalFormat("#.##");

    public NumberRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public boolean shouldRender(DummyNumberEntity numberEntity, Frustum pCamera, double pCamX, double pCamY, double pCamZ) {
        if (numberEntity.canPlayerSee(Minecraft.getInstance().player)) {
            return super.shouldRender(numberEntity, pCamera, pCamX, pCamY, pCamZ);
        }
        return false;
    }

    @Override
    public void render(DummyNumberEntity entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn,
                       int packedLightIn) {
        Font fontRenderer = this.getFont();

        matrixStackIn.pushPose();
        // translate towards player
        //PlayerEntity player = Minecraft.getInstance().player;


        //Vector3d v = (player.getPositionVec().subtract(entityIn.getPositionVec())).normalize();
        //matrixStackIn.translate(v.getX(), v.getY(), v.getZ());


        double d = Math.sqrt(this.entityRenderDispatcher.distanceToSqr(entityIn.getX(), entityIn.getY(), entityIn.getZ()));

        double inc = Mth.clamp(d / 32f, 0, 5f);

        // animation
        matrixStackIn.translate(0, (1 + inc / 4f) * Mth.lerp(partialTicks, entityIn.prevDy, entityIn.dy), 0);
        // rotate towards camera

        float fadeout = Mth.lerp(partialTicks, entityIn.prevFadeout, entityIn.fadeout);

        float defScale = 0.006f;
        float scale = (float) (defScale * d);
        matrixStackIn.mulPose(this.entityRenderDispatcher.cameraOrientation());
        // matrixStackIn.translate(0, 0, -1);
        // animation
        matrixStackIn.translate((1 + inc) * Mth.lerp(partialTicks, entityIn.prevDx, entityIn.dx), 0, 0);
        // scale depending on distance so size remains the same
        matrixStackIn.scale(-scale, -scale, scale);
        matrixStackIn.translate(0, (4d * (1 - fadeout)), 0);
        matrixStackIn.scale(fadeout, fadeout, fadeout);
        matrixStackIn.translate(0, -d / 10d, 0);

        float number = Configs.Cached.SHOW_HEARTHS ? entityIn.getNumber() / 2f : entityIn.getNumber();
        String s = df.format(number);
        // center string
        matrixStackIn.translate((-fontRenderer.width(s) / 2f) + 0.5f, 0, 0);
        fontRenderer.drawInBatch(s, 0, 0, entityIn.color.getColor(), true, matrixStackIn.last().pose(), bufferIn, false, 0, packedLightIn);
        // matrixStackIn.translate(fontRenderer.getStringWidth(s) / 2, 0, 0);
        matrixStackIn.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(DummyNumberEntity entity) {
        return null;
    }
}
