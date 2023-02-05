package net.mehvahdjukaar.dummmmmmy.forge;

import net.mehvahdjukaar.dummmmmmy.Dummmmmmy;
import net.mehvahdjukaar.dummmmmmy.DummmmmmyClient;
import net.mehvahdjukaar.dummmmmmy.common.ModEvents;
import net.mehvahdjukaar.moonlight.api.platform.PlatformHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * Author: MehVahdJukaar
 */
@Mod(Dummmmmmy.MOD_ID)
public class DummmmmmyForge {

    public DummmmmmyForge() {
        Dummmmmmy.commonInit();

        if (PlatformHelper.getEnv().isClient()) {
            DummmmmmyClient.init();
        }
        MinecraftForge.EVENT_BUS.register(this);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(DummmmmmyForge::setup);
    }

    public static void setup(final FMLCommonSetupEvent event) {
        event.enqueueWork(Dummmmmmy::commonSetup);
    }

    @SubscribeEvent
    public void onEntityCriticalHit(CriticalHitEvent event) {
        ModEvents.onEntityCriticalHit(event.getEntity(), event.getTarget(), event.getDamageModifier());
    }

    //prevents them from spawning
    @SubscribeEvent
    public void onCheckSpawn(LivingSpawnEvent.CheckSpawn event) {
       if(ModEvents.onCheckSpawn(event.getEntity(), event.getLevel())){
           event.setResult(Event.Result.DENY);
       }
    }

    //add goal
    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinLevelEvent event) {
        ModEvents.onEntityJoinWorld(event.getEntity());
    }

}

