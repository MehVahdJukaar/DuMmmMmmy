package net.mehvahdjukaar.dummmmmmy.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.mehvahdjukaar.dummmmmmy.Dummmmmmy;
import net.mehvahdjukaar.dummmmmmy.configs.ClientConfigs;
import net.mehvahdjukaar.dummmmmmy.configs.CritMode;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DamageNumberParticle extends Particle {

    public static final ParticleRenderType GROUP = new ParticleRenderType(Dummmmmmy.MOD_ID + ":damage_numbers");

    private static final float ALPHA_FADE_IN_TICKS = 5;
    private static final float SIZE_POP_IN_TICKS = 10;
    private static final float TRAVEL_TICKS = 14;

    private static final List<Float> POSITIONS = new ArrayList<>(Arrays.asList(0f, -0.25f, 0.12f, -0.12f, 0.25f));

    private final Font fontRenderer = Minecraft.getInstance().font;

    private final Component text;
    private final int color;
    private float fadeout = 1;
    private float prevFadeout = 1;

    private final float targetDX;
    private final float targetDY;


    public DamageNumberParticle(ClientLevel clientLevel, double x, double y, double z,
                                double amount, double dColor, double dz) {
        super(clientLevel, x, y, z);
        this.lifetime = 35;
        this.color = amount < 0 ? 0xff00ff00 : (int) dColor;

        double number = Math.abs(ClientConfigs.SHOW_HEARTHS.get() ? amount / 2f : amount);
        boolean bold = ClientConfigs.CRIT_BOLD.get();
        this.yd = 1;

        int index = CritMode.extractIntegerPart(dz);
        float critMult = CritMode.extractFloatPart(dz);
        if (critMult == 0) {
            this.text = Component.literal((amount < 0 ? "+" : "") + Dummmmmmy.DF2.format(number));
        } else {
            switch (ClientConfigs.CRIT_MODE.get()) {
                case COLOR -> {
                    this.text = Component.literal((amount < 0 ? "+" : "") + Dummmmmmy.DF2.format(number))
                            .setStyle(bold ? Style.EMPTY.withBold(bold) : Style.EMPTY);
                }
                case COLOR_AND_MULTIPLIER -> {
                    this.text = Component.translatable("message.dummmmmmy.crit",
                                    Dummmmmmy.DF1.format(number), Dummmmmmy.DF1.format(critMult))
                            .setStyle(bold ? Style.EMPTY.withBold(bold) : Style.EMPTY);
                }
                default -> {
                    this.text = Component.literal((amount < 0 ? "+" : "") + Dummmmmmy.DF2.format(number));
                }
            }
        }

        this.xd = POSITIONS.get(Math.floorMod(index, POSITIONS.size()));

        float stepX = (float) this.xd;
        float stepY = (float) this.yd;
        float driftX = 0;
        float driftY = 0;
        while (true) {
            driftX += stepX;
            driftY += stepY;
            if (!insideTorsoEllipse(driftX, driftY)) break;
            stepY /= 2;
        }
        this.targetDX = driftX;
        this.targetDY = driftY;
    }

    private static boolean insideTorsoEllipse(float dx, float dy) {
        return Math.sqrt(Mth.square(dx * 1.5) + Mth.square(dy - 1)) < 0.9;
    }

    public State extract(Camera camera, float partialTicks) {
        Vec3 cameraPos = camera.position();
        float particleX = (float) (Mth.lerp(partialTicks, this.xo, this.x) - cameraPos.x());
        float particleY = (float) (Mth.lerp(partialTicks, this.yo, this.y) - cameraPos.y());
        float particleZ = (float) (Mth.lerp(partialTicks, this.zo, this.z) - cameraPos.z());

        int light = ClientConfigs.LIT_UP_PARTICLES.get() ? LightCoordsUtil.FULL_BRIGHT : this.getLightCoords(partialTicks);

        PoseStack poseStack = new PoseStack();
        poseStack.translate(particleX, particleY, particleZ);

        double distanceFromCam = new Vec3(particleX, particleY, particleZ).length();

        double inc = Mth.clamp(distanceFromCam / 32f, 0, 5f);

        float animAge = this.age + partialTicks;
        float travel = easeOutQuint(Math.min(1, animAge / TRAVEL_TICKS));

        // animation
        poseStack.translate(0, (1 + inc / 4f) * this.targetDY * travel, 0);
        // rotate towards camera

        float fade = Mth.lerp(partialTicks, this.prevFadeout, this.fadeout);
        float popIn = easeOutBack(Math.min(1, animAge / SIZE_POP_IN_TICKS));
        float appear = Math.min(1, animAge / ALPHA_FADE_IN_TICKS);
        float alphaIn = 1 - (1 - appear) * (1 - appear);

        float defScale = 0.006f;
        float scale = (float) (defScale * distanceFromCam);
        poseStack.mulPose(camera.rotation());
        poseStack.translate(0, 0, 0.35f);

        // animation
        poseStack.translate((1 + inc) * this.targetDX * travel, 0, 0);
        // scale depending on distance so size remains the same
        poseStack.scale(scale, -scale, scale);
        poseStack.translate(0, (4d * (1 - fade)), 0);
        poseStack.scale(fade, fade, fade);
        poseStack.scale(popIn, popIn, popIn);
        poseStack.translate(0, -distanceFromCam / 10d, 0);

        float x1 = 0.5f - fontRenderer.width(text) / 2f;
        return new State(poseStack, text.getVisualOrderText(), x1, withAlpha(this.color, alphaIn * fade), light);
    }

    private static float easeOutQuint(float t) {
        float p = 1 - t;
        return 1 - p * p * p * p * p;
    }

    private static float easeOutBack(float t) {
        float p = t - 1;
        return 1 + 2.7f * p * p * p + 1.7f * p * p;
    }

    // font forces full opacity when the top alpha bits are all off, so never go below 4
    private static int withAlpha(int color, float mult) {
        int alpha = Mth.clamp((int) (((color >>> 24) & 0xff) * mult), 4, 255);
        return (color & 0x00ffffff) | (alpha << 24);
    }

    public record State(PoseStack pose, FormattedCharSequence text, float x, int color, int light) {
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            float length = 6;
            this.prevFadeout = this.fadeout;
            this.fadeout = this.age > (lifetime - length) ? ((float) lifetime - this.age) / length : 1;
        }
    }

    @Override
    public ParticleRenderType getGroup() {
        return GROUP;
    }


    public static class Factory implements ParticleProvider<SimpleParticleType> {
        public Factory(SpriteSet spriteSet) {
        }

        @Override
        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z,
                                       double xSpeed, double ySpeed, double zSpeed, RandomSource random) {
            return new DamageNumberParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
        }
    }
}
