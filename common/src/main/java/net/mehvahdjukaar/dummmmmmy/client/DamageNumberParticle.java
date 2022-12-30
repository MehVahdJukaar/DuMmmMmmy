package net.mehvahdjukaar.dummmmmmy.client;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;

public class DamageNumberParticle extends Particle {
    protected DamageNumberParticle(ClientLevel clientLevel, double d, double e, double f) {
        super(clientLevel, d, e, f);
    }

    public DamageNumberParticle(ClientLevel clientLevel, double d, double e, double f, double g, double h, double i) {
        super(clientLevel, d, e, f, g, h, i);
    }

    @Override
    public void render(VertexConsumer buffer, Camera renderInfo, float partialTicks) {

    }

    @Override
    public ParticleRenderType getRenderType() {
        return null;
    }
}
