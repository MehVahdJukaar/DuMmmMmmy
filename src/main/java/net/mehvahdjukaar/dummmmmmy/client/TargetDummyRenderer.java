package net.mehvahdjukaar.dummmmmmy.client;

import net.mehvahdjukaar.dummmmmmy.common.Configs;
import net.mehvahdjukaar.dummmmmmy.entity.TargetDummyEntity;
import net.mehvahdjukaar.dummmmmmy.setup.ClientHandler;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;

public class TargetDummyRenderer extends HumanoidMobRenderer<TargetDummyEntity, TargetDummyModel<TargetDummyEntity>> {

    public TargetDummyRenderer(EntityRendererProvider.Context context) {
        super(context, new TargetDummyModel<>(context.bakeLayer(ClientHandler.DUMMY_BODY)), 0);
        this.addLayer(new LayerDummyArmor<>(this,
                new TargetDummyModel<>(context.bakeLayer(ClientHandler.DUMMY_ARMOR_INNER)),
                new TargetDummyModel<>(context.bakeLayer(ClientHandler.DUMMY_ARMOR_OUTER))));
    }


    @Override
    public ResourceLocation getTextureLocation(TargetDummyEntity entity) {
        return Configs.Cached.SKIN.getSkin(entity.isSheared());
    }

}
