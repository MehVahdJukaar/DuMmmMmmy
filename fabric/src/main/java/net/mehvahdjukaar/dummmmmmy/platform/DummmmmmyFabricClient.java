package net.mehvahdjukaar.dummmmmmy.platform;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleGroupRegistry;
import net.mehvahdjukaar.dummmmmmy.client.DamageNumberParticle;
import net.mehvahdjukaar.dummmmmmy.client.DamageNumberParticleGroup;

public class DummmmmmyFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ParticleGroupRegistry.register(DamageNumberParticle.GROUP, DamageNumberParticleGroup::new);
    }
}
