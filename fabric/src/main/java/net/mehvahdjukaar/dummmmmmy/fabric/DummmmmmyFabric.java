package net.mehvahdjukaar.dummmmmmy.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.mehvahdjukaar.dummmmmmy.Dummmmmmy;
import net.mehvahdjukaar.dummmmmmy.DummmmmmyClient;
import net.mehvahdjukaar.moonlight.api.platform.PlatformHelper;
import net.mehvahdjukaar.moonlight.fabric.FabricSetupCallbacks;

public class DummmmmmyFabric implements ModInitializer {

    @Override
    public void onInitialize() {

        Dummmmmmy.commonInit();

        if (PlatformHelper.getEnv().isClient()) {
            FabricSetupCallbacks.CLIENT_SETUP.add(DummmmmmyClient::init);
        }

        UseBlockCallback.EVENT.register(Dummmmmmy::onRightClickBlock);

        FabricSetupCallbacks.finishModInit(Dummmmmmy.MOD_ID);

    }
}
