package net.mehvahdjukaar.dummmmmmy.client;

import net.mehvahdjukaar.dummmmmmy.configs.ClientConfigs;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import org.jetbrains.annotations.Nullable;

public class TargetDummyRenderState extends HumanoidRenderState {

    public Identifier texture = ClientConfigs.SkinType.DEFAULT.getSkin(false);
    public boolean blocking;
    public float shake;
    public float swing;
    public float recharging;
    public float headYaw;
    public float headPitch;
    public boolean chestOpenForCape;

    public @Nullable DyeColor bannerColor;
    public BannerPatternLayers bannerPatterns = BannerPatternLayers.EMPTY;
}
