package net.mehvahdjukaar.dummmmmmy.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.mehvahdjukaar.dummmmmmy.Dummmmmmy;
import net.mehvahdjukaar.dummmmmmy.DummmmmmyClient;
import net.mehvahdjukaar.dummmmmmy.common.ModEvents;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.mehvahdjukaar.moonlight.fabric.MLFabricSetupCallbacks;

public class DummmmmmyFabric implements ModInitializer {

    @Override
    public void onInitialize() {

        Dummmmmmy.commonInit();

        if (PlatHelper.getPhysicalSide().isClient()) {
            MLFabricSetupCallbacks.CLIENT_SETUP.add(DummmmmmyClient::init);
        }

        MLFabricSetupCallbacks.COMMON_SETUP.add(Dummmmmmy::commonSetup);

        if (PlatHelper.isModLoaded("lithium")) {
            Dummmmmmy.LOGGER.warn("Lithium detected. MmmMmmMmmMmm scarecrow mode has been disabled as lithium doesnt have a way to add goals to new entities");
        } else {
            ServerEntityEvents.ENTITY_LOAD.register((e, w) -> ModEvents.onEntityJoinWorld(e));
        }
    }
}
