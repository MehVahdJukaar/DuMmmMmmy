package net.mehvahdjukaar.dummmmmmy.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import net.minecraft.client.renderer.entity.layers.WingsLayer;

public class LayerDummyElytra extends WingsLayer<TargetDummyRenderState, TargetDummyModel> {

    public LayerDummyElytra(RenderLayerParent<TargetDummyRenderState, TargetDummyModel> renderer,
                            EntityModelSet modelSet, EquipmentLayerRenderer equipmentRenderer) {
        super(renderer, modelSet, equipmentRenderer);
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector collector, int lightCoords,
                       TargetDummyRenderState state, float yRot, float xRot) {
        poseStack.pushPose();
        poseStack.translate(0, -12 / 16f, 0);
        this.getParentModel().getBody().translateAndRotate(poseStack);
        super.submit(poseStack, collector, lightCoords, state, yRot, xRot);
        poseStack.popPose();
    }
}
