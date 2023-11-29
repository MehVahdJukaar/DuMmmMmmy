package net.mehvahdjukaar.dummmmmmy.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.mehvahdjukaar.dummmmmmy.Dummmmmmy;
import net.mehvahdjukaar.dummmmmmy.common.ModEvents;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.mehvahdjukaar.moonlight.api.platform.fabric.ClientHelperImpl;

public class DummmmmmyFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        Dummmmmmy.init();

        if (PlatHelper.isModLoaded("lithium")) {
            Dummmmmmy.LOGGER.warn("Lithium detected. MmmMmmMmmMmm scarecrow mode has been disabled as lithium doesnt have a way to add goals to new entities");
        } else {
            //TODO: fix this causing concurrency issues
           // ServerEntityEvents.ENTITY_LOAD.register((e, w) -> ModEvents.onEntityJoinWorld(e));
        }
    }
}
