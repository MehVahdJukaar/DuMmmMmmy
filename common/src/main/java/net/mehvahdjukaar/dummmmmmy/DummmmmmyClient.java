package net.mehvahdjukaar.dummmmmmy;

import net.mehvahdjukaar.dummmmmmy.client.DamageNumberParticle;
import net.mehvahdjukaar.dummmmmmy.client.DummyShowcaseWidget;
import net.mehvahdjukaar.dummmmmmy.client.HayParticle;
import net.mehvahdjukaar.dummmmmmy.client.TargetDummyModel;
import net.mehvahdjukaar.dummmmmmy.client.TargetDummyRenderer;
import net.mehvahdjukaar.moonlight.api.client.gui.ConfigScreenExtensions;
import net.mehvahdjukaar.moonlight.api.platform.ClientHelper;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.entity.ArmorModelSet;

public class DummmmmmyClient {

    public static void init() {
        ClientHelper.addModelLayerRegistration(DummmmmmyClient::registerLayers);
        ClientHelper.addEntityRenderersRegistration(DummmmmmyClient::registerEntityRenderers);
        ClientHelper.addParticleRegistration(DummmmmmyClient::registerParticles);
        // a punchable dummy instead of the mod icon on the config screen
        ConfigScreenExtensions.registerShowcase(Dummmmmmy.MOD_ID,
                (modId, x, y, width, height) -> new DummyShowcaseWidget(x, y, width, height));
    }


    public static void setup() {

    }

    private static ModelLayerLocation loc(String name) {
        return new ModelLayerLocation(Dummmmmmy.res(name), name);
    }

    public static final ModelLayerLocation DUMMY_BODY = loc("dummy");
    public static final ArmorModelSet<ModelLayerLocation> DUMMY_ARMOR = new ArmorModelSet<>(
            loc("dummy_armor_head"), loc("dummy_armor_chest"), loc("dummy_armor_legs"), loc("dummy_armor_feet"));


    private static void registerLayers(ClientHelper.ModelLayerEvent event) {
        event.register(DUMMY_BODY, TargetDummyModel::createBodyLayer);
        ArmorModelSet<LayerDefinition> armor = TargetDummyModel.createArmorLayers();
        event.register(DUMMY_ARMOR.head(), armor::head);
        event.register(DUMMY_ARMOR.chest(), armor::chest);
        event.register(DUMMY_ARMOR.legs(), armor::legs);
        event.register(DUMMY_ARMOR.feet(), armor::feet);
    }

    private static void registerEntityRenderers(ClientHelper.EntityRendererEvent event) {
        event.register(Dummmmmmy.TARGET_DUMMY.get(), TargetDummyRenderer::new);
    }


    private static void registerParticles(ClientHelper.ParticleEvent event) {
        event.register(Dummmmmmy.NUMBER_PARTICLE.get(), DamageNumberParticle.Factory::new);
        event.register(Dummmmmmy.HAY_PARTICLE.get(), HayParticle.Factory::new);
    }


}
