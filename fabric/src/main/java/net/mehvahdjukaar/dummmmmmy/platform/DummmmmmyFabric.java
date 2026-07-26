package net.mehvahdjukaar.dummmmmmy.platform;

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
            Dummmmmmy.LOGGER.warn("Lithium detected. MmmMmmMmmMmm scarecrow mode might not work well due to a lithium bug (the mod doesnt have a way to add goals to new entities)");
        }


    }
}
