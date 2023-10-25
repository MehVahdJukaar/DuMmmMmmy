package net.mehvahdjukaar.dummmmmmy.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
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
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DamageNumberParticle extends Particle {

    private static final List<Float> POSITIONS = new ArrayList<>(Arrays.asList(0f, -0.25f, 0.12f, -0.12f, 0.25f));
    private static final DecimalFormat DF2 = new DecimalFormat("#.##");
    private static final DecimalFormat DF1 = new DecimalFormat("#.#");

    private final Font fontRenderer = Minecraft.getInstance().font;

    private final Component text;
    private final int color;
    private final int darkColor;
    private float fadeout = -1;
    private float prevFadeout = -1;

    //visual offset
    private float visualDY = 0;
    private float prevVisualDY = 0;
    private float visualDX = 0;
    private float prevVisualDX = 0;


    public DamageNumberParticle(ClientLevel clientLevel, double x, double y, double z,
                                double amount, double dColor, double dz) {
        super(clientLevel, x, y, z);
        this.lifetime = 35;
        //this.setColor(FastColor.ARGB32.red(color), FastColor.ARGB32.green(color), FastColor.ARGB32.blue(color));
        this.color = amount<0 ? 0xff00ff00 : (int) dColor ;
        this.darkColor = FastColor.ARGB32.color(255, (int) (this.rCol * 0.25f), (int) (this.rCol * 0.25f), (int) (this.rCol * 0.25));

        double number = Math.abs(ClientConfigs.SHOW_HEARTHS.get() ? amount / 2f : amount);

        this.yd = 1;

        int index = CritMode.extractIntegerPart(dz);
        float critMult = CritMode.extractFloatPart(dz);
        if (critMult == 0) {
            this.text =  Component.literal((amount<0 ? "+" : "") +DF2.format(number));
        } else {
            this.text = Component.translatable("message.dummmmmmy.crit", DF1.format(number), DF1.format(critMult));
        }

        this.xd = POSITIONS.get((index % POSITIONS.size()));
    }

    @Override
    public void render(VertexConsumer consumer, Camera camera, float partialTicks) {

        Vec3 cameraPos = camera.getPosition();
        float particleX = (float) (Mth.lerp(partialTicks, this.xo, this.x) - cameraPos.x());
        float particleY = (float) (Mth.lerp(partialTicks, this.yo, this.y) - cameraPos.y());
        float particleZ = (float) (Mth.lerp(partialTicks, this.zo, this.z) - cameraPos.z());


        int light = ClientConfigs.LIT_UP_PARTICLES.get() ? LightTexture.FULL_BRIGHT : this.getLightColor(partialTicks);


        PoseStack poseStack = new PoseStack();
        poseStack.pushPose();
        poseStack.translate(particleX, particleY, particleZ);


        double distanceFromCam = new Vec3(particleX, particleY, particleZ).length();

        double inc = Mth.clamp(distanceFromCam / 32f, 0, 5f);

        // animation
        poseStack.translate(0, (1 + inc / 4f) * Mth.lerp(partialTicks, this.prevVisualDY, this.visualDY), 0);
        // rotate towards camera

        float fadeout = Mth.lerp(partialTicks, this.prevFadeout, this.fadeout);

        float defScale = 0.006f;
        float scale = (float) (defScale * distanceFromCam);
        poseStack.mulPose(camera.rotation());

        // animation
        poseStack.translate((1 + inc) * Mth.lerp(partialTicks, this.prevVisualDX, this.visualDX), 0, 0);
        // scale depending on distance so size remains the same
        poseStack.scale(-scale, -scale, scale);
        poseStack.translate(0, (4d * (1 - fadeout)), 0);
        poseStack.scale(fadeout, fadeout, fadeout);
        poseStack.translate(0, -distanceFromCam / 10d, 0);

        var buffer = Minecraft.getInstance().renderBuffers().bufferSource();

        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(770, 771, 1, 0);

        float x1 = 0.5f - fontRenderer.width(text) / 2f;

        fontRenderer.drawInBatch(text, x1,
                0, color, false,
                poseStack.last().pose(), buffer, Font.DisplayMode.NORMAL, 0, light);
        poseStack.translate(1, 1, +0.03);
        fontRenderer.drawInBatch(text, x1,
                0, darkColor, false,
                poseStack.last().pose(), buffer, Font.DisplayMode.NORMAL, 0, light);

        buffer.endBatch();

        poseStack.popPose();
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

            this.prevVisualDY = this.visualDY;
            this.visualDY += this.yd;
            this.prevVisualDX = this.visualDX;
            this.visualDX += this.xd;

            //spawn numbers in a sort of ellipse centered on his torso
            if (Math.sqrt(Mth.square(this.visualDX * 1.5) + Mth.square(this.visualDY - 1)) < 1.9 - 1) {

                this.yd = this.yd / 2;
            } else {
                this.yd = 0;
                this.xd = 0;
            }
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }


    public static class Factory implements ParticleProvider<SimpleParticleType> {
        public Factory(SpriteSet spriteSet) {
        }

        @Override
        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new DamageNumberParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
        }
    }
}
