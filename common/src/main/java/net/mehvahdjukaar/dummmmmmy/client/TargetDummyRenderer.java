package net.mehvahdjukaar.dummmmmmy.client;

import net.mehvahdjukaar.dummmmmmy.DummmmmmyClient;
import net.mehvahdjukaar.dummmmmmy.common.TargetDummyEntity;
import net.mehvahdjukaar.dummmmmmy.configs.ClientConfigs;
import net.minecraft.client.renderer.entity.ArmorModelSet;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.layers.WingsLayer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BannerPatternLayers;

public class TargetDummyRenderer extends HumanoidMobRenderer<TargetDummyEntity, TargetDummyRenderState, TargetDummyModel> {

    public TargetDummyRenderer(EntityRendererProvider.Context context) {
        super(context, new TargetDummyModel(context.bakeLayer(DummmmmmyClient.DUMMY_BODY)), 0);
        ArmorModelSet<TargetDummyModel> armor = ArmorModelSet.bake(DummmmmmyClient.DUMMY_ARMOR,
                context.getModelSet(), TargetDummyModel::new);
        this.addLayer(new HumanoidArmorLayer<>(this, armor, context.getEquipmentRenderer()));
        // hand items and elytra are rendered by our own layers below
        this.layers.removeIf(layer -> layer instanceof ItemInHandLayer || layer instanceof WingsLayer);
        this.addLayer(new LayerDummyShield(this));
        this.addLayer(new LayerDummyCape(this, context));
        this.addLayer(new LayerDummyElytra(this, context.getModelSet(), context.getEquipmentRenderer()));
    }

    @Override
    public TargetDummyRenderState createRenderState() {
        return new TargetDummyRenderState();
    }

    @Override
    public void extractRenderState(TargetDummyEntity entity, TargetDummyRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        // no red flash when hit, same as the creaking
        state.hasRedOverlay = false;
        state.texture = ClientConfigs.SKIN.get().getSkin(entity.isSheared());
        state.blocking = entity.isBlocking();
        state.shake = entity.getShake(partialTicks);
        state.swing = entity.getAnimationPosition(partialTicks);
        state.recharging = entity.getRechargingAnimation(partialTicks);

        ItemStack chest = entity.getItemBySlot(EquipmentSlot.CHEST);
        state.chestOpenForCape = chest.isEmpty() || chest.has(DataComponents.GLIDER);

        ItemStack offhand = entity.getOffhandItem();
        if (offhand.getItem() instanceof BannerItem banner) {
            state.bannerColor = banner.getColor();
            state.bannerPatterns = offhand.getOrDefault(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY);
        } else {
            state.bannerColor = null;
            state.bannerPatterns = BannerPatternLayers.EMPTY;
        }
    }

    @Override
    public Identifier getTextureLocation(TargetDummyRenderState state) {
        return state.texture;
    }

}
