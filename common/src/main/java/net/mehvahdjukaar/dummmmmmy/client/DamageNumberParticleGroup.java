package net.mehvahdjukaar.dummmmmmy.client;

import net.minecraft.client.Camera;
import net.minecraft.client.gui.Font;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleGroup;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.state.level.ParticleGroupRenderState;

import java.util.List;

public class DamageNumberParticleGroup extends ParticleGroup<DamageNumberParticle> {

    public DamageNumberParticleGroup(ParticleEngine engine) {
        super(engine);
    }

    @Override
    public ParticleGroupRenderState extractRenderState(Frustum frustum, Camera camera, float partialTicks) {
        return new State(this.particles.stream().map(p -> p.extract(camera, partialTicks)).toList());
    }

    private record State(List<DamageNumberParticle.State> numbers) implements ParticleGroupRenderState {
        @Override
        public void submit(SubmitNodeCollector collector, CameraRenderState camera) {
            for (var n : this.numbers) {
                collector.submitText(n.pose(), n.x(), 0, n.text(), true, Font.DisplayMode.NORMAL,
                        n.light(), n.color(), 0, 0);
            }
        }
    }
}
