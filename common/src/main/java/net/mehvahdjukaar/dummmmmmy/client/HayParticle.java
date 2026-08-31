package net.mehvahdjukaar.dummmmmmy.client;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public class HayParticle extends SingleQuadParticle {

    private float rotSpeed;

    protected HayParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed,
                          TextureAtlasSprite sprite) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed, sprite);
        this.xd = xSpeed;
        this.yd = ySpeed;
        this.zd = zSpeed;

        this.hasPhysics = true;
        this.rotSpeed = (float) (0.06f + this.random.nextGaussian() * 0.15f);
        if (this.random.nextBoolean()) this.rotSpeed = -this.rotSpeed;
        this.lifetime = 40 + this.random.nextInt(20);
        float f = this.random.nextFloat() * 0.05f + 0.1f;
        this.quadSize = f;
        this.setSize(f, f);
        this.gravity = 0.07f;

        float initialRoll = this.random.nextFloat() * Mth.TWO_PI;
        this.roll = this.oRoll = initialRoll;

        this.bCol = this.rCol = this.gCol = 0.85f;
    }

    @Override
    protected Layer getLayer() {
        return Layer.bySprite(this.sprite);
    }

    @Override
    public void tick() {
        this.oRoll = this.roll;
        this.roll += this.rotSpeed;
        this.rotSpeed *= this.friction;
        super.tick();
    }

    @Override
    public void move(double x, double y, double z) {
        float wantedY = (float) (this.y + y);
        super.move(x, y, z);
        if (Math.abs(wantedY - this.y) > 0.0001) {
            rotSpeed = 0;
        }
    }

    public record Factory(SpriteSet spriteSet) implements ParticleProvider<SimpleParticleType> {

        @Override
        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z,
                                       double xSpeed, double ySpeed, double zSpeed, RandomSource random) {
            return new HayParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, spriteSet.get(random));
        }
    }
}
