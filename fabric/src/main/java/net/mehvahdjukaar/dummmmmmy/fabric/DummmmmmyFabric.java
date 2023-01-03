package net.mehvahdjukaar.dummmmmmy.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
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

        ServerEntityEvents.ENTITY_LOAD.register((e,w)-> ModEvents.onEntityJoinWorld(e));
    }
}
