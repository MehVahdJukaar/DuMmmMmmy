package net.mehvahdjukaar.dummmmmmy.platform;

import net.mehvahdjukaar.dummmmmmy.Dummmmmmy;
import net.mehvahdjukaar.dummmmmmy.client.DamageNumberParticle;
import net.mehvahdjukaar.dummmmmmy.client.DamageNumberParticleGroup;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterParticleGroupsEvent;

@EventBusSubscriber(modid = Dummmmmmy.MOD_ID, value = Dist.CLIENT)
public class DummmmmmyForgeClient {

    @SubscribeEvent
    public static void onRegisterParticleGroups(RegisterParticleGroupsEvent event) {
        event.register(DamageNumberParticle.GROUP, DamageNumberParticleGroup::new);
    }
}
