package net.mehvahdjukaar.dummmmmmy.client;

import net.mehvahdjukaar.dummmmmmy.common.TargetDummyEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.world.entity.EquipmentSlot;

public class LayerDummyArmor<T extends TargetDummyEntity, A extends HumanoidModel<T>> extends HumanoidArmorLayer<T, A, A> {

    public LayerDummyArmor(RenderLayerParent<T, A> renderLayerParent, A modelLegs, A modelChest, ModelManager modelManager) {
        super(renderLayerParent, modelLegs, modelChest, modelManager);
        if (modelChest instanceof TargetDummyModel<?> m) m.standPlate.visible = false;
        if (modelLegs instanceof TargetDummyModel<?> m2) m2.standPlate.visible = false;
    }

    @Override
    public void setPartVisibility(A modelIn, EquipmentSlot slotIn) {
        modelIn.setAllVisible(false);
        modelIn.rightLeg.visible = false;
        switch (slotIn) {
            case HEAD -> modelIn.head.visible = true;
            case CHEST -> {
                modelIn.body.visible = true;
                modelIn.rightArm.visible = true;
                modelIn.leftArm.visible = true;
            }
            case LEGS -> {
                modelIn.body.visible = true;
                modelIn.leftLeg.visible = true;
            }
            case FEET -> {
                modelIn.leftLeg.visible = true;
                modelIn.body.visible = false;
            }
        }
    }

}