package net.mehvahdjukaar.dummmmmmy.forge;

import net.mehvahdjukaar.dummmmmmy.Dummmmmmy;
import net.mehvahdjukaar.dummmmmmy.DummmmmmyClient;
import net.mehvahdjukaar.dummmmmmy.common.ModEvents;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.mehvahdjukaar.moonlight.api.platform.RegHelper;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.BabyEntitySpawnEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
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

        if (PlatHelper.getPhysicalSide().isClient()) {
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
    public void onCheckSpawn(MobSpawnEvent.FinalizeSpawn event) {
       if(ModEvents.onCheckSpawn(event.getEntity(), event.getLevel())){
           event.setSpawnCancelled(true);
       }
    }

    //add goal
    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinLevelEvent event) {
        ModEvents.onEntityJoinWorld(event.getEntity());
    }

    @SubscribeEvent
    public void onEntityHit(LivingDamageEvent event){
        ModEvents.onEntityDamage(event.getEntity(), event.getAmount(), event.getSource());
    }

}

