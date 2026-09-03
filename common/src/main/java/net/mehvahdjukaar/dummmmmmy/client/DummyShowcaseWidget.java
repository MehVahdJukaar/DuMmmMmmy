package net.mehvahdjukaar.dummmmmmy.client;

import net.mehvahdjukaar.dummmmmmy.Dummmmmmy;
import net.mehvahdjukaar.dummmmmmy.configs.ClientConfigs;
import net.mehvahdjukaar.moonlight.api.client.gui.particle.ScreenParticle;
import net.mehvahdjukaar.moonlight.api.client.gui.particle.ScreenParticleEngine;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class DummyShowcaseWidget extends AbstractWidget {

    private static final float BLOCKS_TALL = 2.15f;
    private static final float GROUND_MARGIN = 3.0f;
    // same swing units, cap and decay the entity uses
    private static final float HIT_DAMAGE = 7f;
    private static final float MAX_SWING = 60f;
    private static final float DECAY_PER_TICK = 0.8f;
    // how much of the look at rotation the body takes, the head takes the rest
    private static final float BODY_SHARE = 0.35f;
    private static final int[] STRAW_COLORS = {0xE3C574, 0xC9A24C, 0xF2E0A5, 0xA8842F};

    private final TargetDummyRenderState dummy = new TargetDummyRenderState();
    private final ScreenParticleEngine particles = new ScreenParticleEngine();
    private final RandomSource random = RandomSource.create();

    private float swing;
    private float shakePhase;
    private float lookYaw;
    private float lookPitch;
    private long lastMs = -1;

    public DummyShowcaseWidget(int x, int y, int width, int height) {
        super(x, y, width, height, Component.translatable("entity.dummmmmmy.target_dummy"));
        this.dummy.entityType = Dummmmmmy.TARGET_DUMMY.get();
    }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        advance(mouseX, mouseY);

        this.dummy.texture = ClientConfigs.SKIN.get().getSkin(false);
        this.dummy.shake = this.shakePhase;
        this.dummy.swing = this.swing;
        // 180 = facing the camera, like vanilla does in InventoryScreen
        this.dummy.bodyRot = 180 + this.lookYaw * Mth.RAD_TO_DEG * BODY_SHARE;
        this.dummy.headYaw = this.lookYaw * (1 - BODY_SHARE);
        this.dummy.headPitch = this.lookPitch;

        float scale = this.height / BLOCKS_TALL;
        Quaternionf rotation = new Quaternionf().rotateZ(Mth.PI);
        Vector3f translation = new Vector3f(0, (this.height / 2f - GROUND_MARGIN) / scale, 0);

        graphics.entity(this.dummy, scale, translation, rotation, null,
                this.getX(), this.getY(), this.getRight(), this.getBottom());

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
    public void onClick(MouseButtonEvent event, boolean doubleClick) {
        this.swing = Math.min(this.swing + HIT_DAMAGE, MAX_SWING);
        this.shakePhase = 0;
        for (int i = 0; i < 12; i++) {
            this.particles.add(ScreenParticle.square((float) event.x(), (float) event.y())
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
