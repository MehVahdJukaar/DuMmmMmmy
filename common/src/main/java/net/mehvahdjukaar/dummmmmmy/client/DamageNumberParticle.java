package net.mehvahdjukaar.dummmmmmy.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.mehvahdjukaar.dummmmmmy.configs.ClientConfigs;
import net.mehvahdjukaar.dummmmmmy.common.DamageType;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DamageNumberParticle extends Particle {

    private static final List<Float> POSITIONS = new ArrayList<>(Arrays.asList(0f, -0.25f, 0.12f, -0.12f, 0.25f));
    private static final DecimalFormat DF = new DecimalFormat("#.##");

    private final Font fontRenderer = Minecraft.getInstance().font;

    private final String text;
    private final int color;
    private final int darkColor;
    private float fadeout = -1;
    private float prevFadeout = -1;

    //idk what these do anymore...
    private float speed = 1;
    private float speedX;

    //visual offset
    private float dy = 0;
    private float prevDy = 0;
    private float dx = 0;
    private float prevDx = 0;


    public DamageNumberParticle(ClientLevel clientLevel, double x, double y, double z,
                                double amount, double damageType, double index) {
        super(clientLevel, x, y, z);
        this.lifetime = 35;
        int color = DamageType.values()[(int) damageType].getColor();
        this.setColor(FastColor.ARGB32.red(color), FastColor.ARGB32.green(color), FastColor.ARGB32.blue(color));
        this.color = color;
        this.darkColor = FastColor.ARGB32.color((int) (this.rCol*0.25f),(int)(this.rCol*0.25f),(int)(this.rCol*0.25),1);

        double number = ClientConfigs.SHOW_HEARTHS.get() ? amount / 2f : amount;
        this.text = DF.format(number);

        this.speedX = POSITIONS.get((int) (index % POSITIONS.size()));
    }

    @Override
    public void render(VertexConsumer consumer, Camera camera, float partialTicks) {
        Vec3 cameraPos = camera.getPosition();
        float particleX = (float) (Mth.lerp(partialTicks, this.xo, this.x) - cameraPos.x());
        float particleY = (float) (Mth.lerp(partialTicks, this.yo, this.y) - cameraPos.y());
        float particleZ = (float) (Mth.lerp(partialTicks, this.zo, this.z) - cameraPos.z());

        var buffer = Minecraft.getInstance().renderBuffers().bufferSource();

        int light = this.getLightColor(1);

        PoseStack poseStack = new PoseStack();
        poseStack.pushPose();
        poseStack.translate(particleX, particleY, particleZ);


        double distanceFromCam = new Vec3(particleX, particleY, particleZ).length();

        double inc = Mth.clamp(distanceFromCam / 32f, 0, 5f);

        // animation
        poseStack.translate(0, (1 + inc / 4f) * Mth.lerp(partialTicks, this.prevDy, this.dy), 0);
        // rotate towards camera

        float fadeout = Mth.lerp(partialTicks, this.prevFadeout, this.fadeout);

        float defScale = 0.006f;
        float scale = (float) (defScale * distanceFromCam);
        poseStack.mulPose(camera.rotation());

        // animation
        poseStack.translate((1 + inc) * Mth.lerp(partialTicks, this.prevDx, this.dx), 0, 0);
        // scale depending on distance so size remains the same
        poseStack.scale(-scale, -scale, scale);
        poseStack.translate(0, (4d * (1 - fadeout)), 0);
        poseStack.scale(fadeout, fadeout, fadeout);
        poseStack.translate(0, -distanceFromCam / 10d, 0);

        float x1 = 0.5f - fontRenderer.width(text) / 2f;
        fontRenderer.drawInBatch(text, x1,
                0, color, false,
                poseStack.last().pose(), buffer, false, 0, light);
        poseStack.translate(1, 1, +0.03);
        fontRenderer.drawInBatch(text, x1,
                0, darkColor, false,
                poseStack.last().pose(), buffer, false, 0, light);


        poseStack.popPose();
        buffer.endBatch();
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

            this.prevDy = this.dy;
            this.dy += this.speed;
            this.prevDx = this.dx;
            this.dx += this.speedX;

            //spawn numbers in a sort of ellipse centered on his torso
            if (Math.sqrt(Math.pow(this.dx * 1.5, 2) + Math.pow(this.dy - 1, 2)) < 1.9 - 1) {

                speed = speed / 2;
            } else {
                speed = 0;
                speedX = 0;
            }
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_LIT;
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
