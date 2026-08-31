package net.mehvahdjukaar.dummmmmmy.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.HumanoidArm;

public class LayerDummyShield extends RenderLayer<TargetDummyRenderState, TargetDummyModel> {

    public LayerDummyShield(RenderLayerParent<TargetDummyRenderState, TargetDummyModel> renderer) {
        super(renderer);
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector collector, int lightCoords,
                       TargetDummyRenderState state, float yRot, float xRot) {
        ItemStackRenderState item = state.rightHandItemState;
        if (item.isEmpty()) return;

        boolean isBlocking = state.blocking;

        poseStack.pushPose();

        this.getParentModel().translateToHand(state, HumanoidArm.RIGHT, poseStack);
        poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(90 + (isBlocking ? 0 : -30)));

        poseStack.translate(isBlocking ? 0 : -1 / 32f, 3 / 16f, 4 / 16f);
        item.submit(poseStack, collector, lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor);
        poseStack.popPose();
    }

}
