package net.mehvahdjukaar.dummmmmmy.forge;

import dev.shadowsoffire.apotheosis.adventure.AdventureEvents;
import dev.shadowsoffire.attributeslib.impl.AttributeEvents;
import net.mehvahdjukaar.dummmmmmy.Dummmmmmy;
import net.mehvahdjukaar.dummmmmmy.DummmmmmyClient;
import net.mehvahdjukaar.dummmmmmy.common.ModEvents;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.mehvahdjukaar.moonlight.api.platform.configs.ConfigBuilder;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
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
        Dummmmmmy.init();
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onEntityCriticalHit(CriticalHitEvent event) {
       if(!event.isCanceled()) {
           ModEvents.onEntityCriticalHit(event.getEntity(), event.getTarget(), event.getDamageModifier());
       }
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

