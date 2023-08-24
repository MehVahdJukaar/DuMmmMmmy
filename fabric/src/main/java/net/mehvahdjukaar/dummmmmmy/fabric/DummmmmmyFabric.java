package net.mehvahdjukaar.dummmmmmy.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.mehvahdjukaar.dummmmmmy.Dummmmmmy;
import net.mehvahdjukaar.dummmmmmy.common.ModEvents;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;

public class DummmmmmyFabric implements ModInitializer {

    @Override
    public void onInitialize() {

        Dummmmmmy.init();


        if (PlatHelper.isModLoaded("lithium")) {
            Dummmmmmy.LOGGER.warn("Lithium detected. MmmMmmMmmMmm scarecrow mode has been disabled as lithium doesnt have a way to add goals to new entities");
        } else {
            ServerEntityEvents.ENTITY_LOAD.register((e, w) -> ModEvents.onEntityJoinWorld(e));
        }
    }
}
