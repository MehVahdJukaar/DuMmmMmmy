package net.mehvahdjukaar.dummmmmmy.client;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.mehvahdjukaar.dummmmmmy.DummmmmmyClient;
import net.mehvahdjukaar.dummmmmmy.common.TargetDummyEntity;
import net.mehvahdjukaar.dummmmmmy.configs.ClientConfigs;
import net.mehvahdjukaar.moonlight.api.client.gui.particle.ScreenParticle;
import net.mehvahdjukaar.moonlight.api.client.gui.particle.ScreenParticleEngine;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public class DummyShowcaseWidget extends AbstractWidget {

    private static final float BLOCKS_TALL = 2.15f;
    // same swing units, cap and decay the entity uses
    private static final float HIT_DAMAGE = 7f;
    private static final float MAX_SWING = 60f;
    private static final float DECAY_PER_TICK = 0.8f;
    // how much of the look at rotation the body takes, the head takes the rest
    private static final float BODY_SHARE = 0.35f;
    private static final int[] STRAW_COLORS = {0xE3C574, 0xC9A24C, 0xF2E0A5, 0xA8842F};

    private final TargetDummyModel<TargetDummyEntity> model;
    private final ScreenParticleEngine particles = new ScreenParticleEngine();
    private final RandomSource random = RandomSource.create();

    private float swing;
    private float shakePhase;
    private float lookYaw;
    private float lookPitch;
    private long lastMs = -1;

    public DummyShowcaseWidget(int x, int y, int width, int height) {
        super(x, y, width, height, Component.translatable("entity.dummmmmmy.target_dummy"));
        this.model = new TargetDummyModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(DummmmmmyClient.DUMMY_BODY));
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        advance(mouseX, mouseY);

        this.model.setHitAnimation(this.shakePhase, this.swing);
        this.model.setupAnim(null, 0, 0, 0, 0, 0);
        this.model.head.yRot = this.lookYaw * (1 - BODY_SHARE);
        this.model.head.xRot += this.lookPitch;
        this.model.hat.copyFrom(this.model.head);

        float scale = this.height / BLOCKS_TALL;   // px per block
        PoseStack pose = graphics.pose();
        pose.pushPose();
        pose.translate(this.getX() + this.width / 2f, this.getY() + this.height - 1f, 100);
        pose.scale(scale, scale, -scale);
        pose.translate(0, -1.501f, 0);   // model origin is at head height, feet 1.5 blocks below it
        pose.mulPose(Axis.YP.rotationDegrees(this.lookYaw * Mth.RAD_TO_DEG * BODY_SHARE));

        Lighting.setupForEntityInInventory();
        MultiBufferSource.BufferSource buffer = graphics.bufferSource();
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(ClientConfigs.SKIN.get().getSkin(false)));
        this.model.renderToBuffer(pose, consumer, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, -1);
        graphics.flush();
        Lighting.setupFor3DItems();
        pose.popPose();

        this.particles.renderAndTick(graphics);
    }

    private void advance(int mouseX, int mouseY) {
        long now = Util.getMillis();
        float dt = this.lastMs < 0 ? 0 : Math.min((now - this.lastMs) / 1000f, 0.1f); // clamped so reopening the screen doesn't jump
        this.lastMs = now;
        float ticks = dt * 20f;

        if (this.swing > 0) {
            this.shakePhase += ticks;
            this.swing -= DECAY_PER_TICK * ticks;
            if (this.swing <= 0) {
                this.swing = 0;
                this.shakePhase = 0;
            }
        }

        float headX = this.getX() + this.width / 2f;
        float headY = this.getY() + this.height * 0.3f;
        float targetYaw = Mth.clamp(-(mouseX - headX) / 70f, -1.1f, 1.1f);
        float targetPitch = Mth.clamp((mouseY - headY) / 90f, -0.5f, 0.5f);
        float ease = Math.min(1f, dt * 8f);
        this.lookYaw = Mth.lerp(ease, this.lookYaw, targetYaw);
        this.lookPitch = Mth.lerp(ease, this.lookPitch, targetPitch);
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        this.swing = Math.min(this.swing + HIT_DAMAGE, MAX_SWING);
        this.shakePhase = 0;
        for (int i = 0; i < 12; i++) {
            this.particles.add(ScreenParticle.square((float) mouseX, (float) mouseY)
                    .velocity(Mth.randomBetween(this.random, -70, 70), Mth.randomBetween(this.random, -90, -20))
                    .gravity(260)
                    .drag(1.5f)
                    .size(Mth.randomBetween(this.random, 1.5f, 3f), 0)
                    .rotation(this.random.nextFloat() * 360)
                    .spin(Mth.randomBetween(this.random, -400, 400))
                    .tint(STRAW_COLORS[this.random.nextInt(STRAW_COLORS.length)])
                    .lifetime(Mth.randomBetween(this.random, 0.4f, 0.8f)));
        }
    }

    @Override
    public void playDownSound(SoundManager handler) {
        handler.play(SimpleSoundInstance.forUI(SoundEvents.ARMOR_STAND_HIT, 1f));
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        output.add(NarratedElementType.TITLE, this.getMessage());
    }
}
