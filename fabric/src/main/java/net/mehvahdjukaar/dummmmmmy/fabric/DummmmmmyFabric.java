package net.mehvahdjukaar.dummmmmmy.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.mehvahdjukaar.dummmmmmy.Dummmmmmy;
import net.mehvahdjukaar.dummmmmmy.DummmmmmyClient;
import net.mehvahdjukaar.dummmmmmy.common.ModEvents;
import net.mehvahdjukaar.moonlight.api.platform.PlatformHelper;
import net.mehvahdjukaar.moonlight.fabric.FabricSetupCallbacks;

public class DummmmmmyFabric implements ModInitializer {

    @Override
    public void onInitialize() {

        Dummmmmmy.commonInit();

        if (PlatformHelper.getEnv().isClient()) {
            FabricSetupCallbacks.CLIENT_SETUP.add(DummmmmmyClient::init);
        }

        FabricSetupCallbacks.COMMON_SETUP.add(Dummmmmmy::commonSetup);

        if (PlatformHelper.isModLoaded("lithium")) {
            Dummmmmmy.LOGGER.warn("Lithium detected. MmmMmmMmmMmm scarecrow mode has been disabled as lithium doesnt have a way to add goals to new entities");
        } else {
            ServerEntityEvents.ENTITY_LOAD.register((e, w) -> ModEvents.onEntityJoinWorld(e));
        }
    }
}
