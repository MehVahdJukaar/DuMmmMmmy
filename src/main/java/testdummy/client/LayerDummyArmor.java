package testdummy.client;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;

public class LayerDummyArmor extends LayerBipedArmor {
    public LayerDummyArmor(RenderLivingBase<?> rendererIn) {
        super(rendererIn);
    }

    protected void initArmor() {
        this.modelLeggings = new ModelDummy(0.5F, 0.0F, 64, 32);
        this.modelArmor = new ModelDummy(1.0F, 0.0F, 64, 32);

        ((ModelDummy) this.modelArmor).standPlate.showModel = false;
        ((ModelDummy) this.modelLeggings).standPlate.showModel = false;
    }

    protected void setModelVisible(ModelBiped model) {
        super.setModelVisible(model);

        model.bipedRightLeg.showModel = false;
    }
}