package net.mehvahdjukaar.dummmmmmy.platform;

import net.mehvahdjukaar.dummmmmmy.Dummmmmmy;
import net.mehvahdjukaar.dummmmmmy.common.ModEvents;
import net.mehvahdjukaar.moonlight.api.platform.RegHelper;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;
import net.neoforged.neoforge.event.entity.player.CriticalHitEvent;

import java.util.Map;

/**
 * Author: MehVahdJukaar
 */
@Mod(Dummmmmmy.MOD_ID)
public class DummmmmmyForge {

    public DummmmmmyForge(IEventBus bus) {
        RegHelper.startRegisteringFor(bus);
        Dummmmmmy.init();
        NeoForge.EVENT_BUS.register(this);

    }

    @SubscribeEvent
    public void onEntityCriticalHit(CriticalHitEvent event) {
        if (event.isCriticalHit()) {
            float mod = event.getDamageMultiplier();
            if (mod > 1) {
                ModEvents.onEntityCriticalHit(event.getEntity(), event.getTarget(), event.getDamageMultiplier());
            }
        }
    }

    //prevents them from spawning
    @SubscribeEvent
    public void onCheckSpawn(FinalizeSpawnEvent event) {
        if (event.getSpawnType() == MobSpawnType.NATURAL && ModEvents.onCheckSpawn(event.getEntity(), event.getLevel())) {
            event.setSpawnCancelled(true);
        }
    }

    //add goal
    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinLevelEvent event) {
        ModEvents.onEntityJoinWorld(event.getEntity());
    }
}

