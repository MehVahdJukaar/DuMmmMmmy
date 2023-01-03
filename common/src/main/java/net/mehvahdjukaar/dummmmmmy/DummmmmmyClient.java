package net.mehvahdjukaar.dummmmmmy;

import net.mehvahdjukaar.dummmmmmy.client.DamageNumberParticle;
import net.mehvahdjukaar.dummmmmmy.client.TargetDummyModel;
import net.mehvahdjukaar.dummmmmmy.client.TargetDummyRenderer;
import net.mehvahdjukaar.moonlight.api.platform.ClientPlatformHelper;
import net.minecraft.client.model.geom.ModelLayerLocation;

public class DummmmmmyClient {

    public static void init() {
        ClientPlatformHelper.addModelLayerRegistration(DummmmmmyClient::registerLayers);
        ClientPlatformHelper.addEntityRenderersRegistration(DummmmmmyClient::registerEntityRenderers);
        ClientPlatformHelper.addParticleRegistration(DummmmmmyClient::registerParticles);
    }


    public static void setup() {

    }

    private static ModelLayerLocation loc(String name) {
        return new ModelLayerLocation(Dummmmmmy.res(name), name);
    }

    public static final ModelLayerLocation DUMMY_BODY = loc("dummy");
    public static final ModelLayerLocation DUMMY_ARMOR_OUTER = loc("dummy_armor_outer");
    public static final ModelLayerLocation DUMMY_ARMOR_INNER = loc("dummy_armor_inner");


    private static void registerLayers(ClientPlatformHelper.ModelLayerEvent event) {
        event.register(DUMMY_BODY, () -> TargetDummyModel.createMesh(0, 64));
        event.register(DUMMY_ARMOR_OUTER, () -> TargetDummyModel.createMesh(1, 32));
        event.register(DUMMY_ARMOR_INNER, () -> TargetDummyModel.createMesh(0.5f, 32));
    }

    private static void registerEntityRenderers(ClientPlatformHelper.EntityRendererEvent event) {
        event.register(Dummmmmmy.TARGET_DUMMY.get(), TargetDummyRenderer::new);
    }


    private static void registerParticles(ClientPlatformHelper.ParticleEvent event) {
        event.register(Dummmmmmy.NUMBER_PARTICLE.get(), DamageNumberParticle.Factory::new);
    }


}
