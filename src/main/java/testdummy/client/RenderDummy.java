package testdummy.client;

import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;
import testdummy.entity.EntityDummy;

public class RenderDummy extends RenderBiped<EntityDummy> {
    public static final ModelDummy model = new ModelDummy(0.0F, 0.0F);
    private static final ResourceLocation texture = new ResourceLocation("testdummy", "textures/entity/dummy.png");

    public RenderDummy(RenderManager renderManager) {
        super(renderManager, model, 0.125F);
        addLayer((LayerRenderer) new LayerDummyArmor(this));
    }

    protected ResourceLocation getEntityTexture(EntityDummy entity) {
        return texture;
    }
}
