package net.mehvahdjukaar.dummmmmmy;

import net.mehvahdjukaar.dummmmmmy.client.DamageNumberParticle;
import net.mehvahdjukaar.dummmmmmy.client.TargetDummyModel;
import net.mehvahdjukaar.dummmmmmy.client.TargetDummyRenderer;
import net.mehvahdjukaar.moonlight.api.platform.ClientHelper;
import net.minecraft.client.model.geom.ModelLayerLocation;

public class DummmmmmyClient {

    public static void init() {
        ClientHelper.addModelLayerRegistration(DummmmmmyClient::registerLayers);
        ClientHelper.addEntityRenderersRegistration(DummmmmmyClient::registerEntityRenderers);
        ClientHelper.addParticleRegistration(DummmmmmyClient::registerParticles);
    }


    public static void setup() {

    }

    private static ModelLayerLocation loc(String name) {
        return new ModelLayerLocation(Dummmmmmy.res(name), name);
    }

    public static final ModelLayerLocation DUMMY_BODY = loc("dummy");
    public static final ModelLayerLocation DUMMY_ARMOR_OUTER = loc("dummy_armor_outer");
    public static final ModelLayerLocation DUMMY_ARMOR_INNER = loc("dummy_armor_inner");


    private static void registerLayers(ClientHelper.ModelLayerEvent event) {
        event.register(DUMMY_BODY, () -> TargetDummyModel.createMesh(0, 64));
        event.register(DUMMY_ARMOR_OUTER, () -> TargetDummyModel.createMesh(1, 32));
        event.register(DUMMY_ARMOR_INNER, () -> TargetDummyModel.createMesh(0.5f, 32));
    }

    private static void registerEntityRenderers(ClientHelper.EntityRendererEvent event) {
        event.register(Dummmmmmy.TARGET_DUMMY.get(), TargetDummyRenderer::new);
    }


    private static void registerParticles(ClientHelper.ParticleEvent event) {
        event.register(Dummmmmmy.NUMBER_PARTICLE.get(), DamageNumberParticle.Factory::new);
    }


}
