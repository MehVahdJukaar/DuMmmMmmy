package net.mehvahdjukaar.dummmmmmy.common;

import net.mehvahdjukaar.dummmmmmy.entity.TargetDummyEntity;
import net.mehvahdjukaar.dummmmmmy.setup.ModRegistry;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class Events {

    @SubscribeEvent
    public static void onEntityCriticalHit(CriticalHitEvent event) {
        if (event != null && event.getEntity() != null) {
            Entity target = event.getTarget();
            if (event.getDamageModifier() == 1.5 && target instanceof TargetDummyEntity dummy) {
                dummy.critical = true;
            }
        }
    }

    public static boolean isScared(Entity entity) {
        String name = entity.getType().getRegistryName().toString();
        return (entity instanceof Animal || Configs.cachedServer.WHITELIST.contains(name))
                && !Configs.cachedServer.BLACKLIST.contains(name);
    }

    public static boolean isScarecrowInRange(Entity entity, Level world) {
        return !world.getEntities(ModRegistry.TARGET_DUMMY.get(), entity.getBoundingBox().inflate(10),
                TargetDummyEntity::isScarecrow).isEmpty();
    }

    //prevents them from spawning
    @SubscribeEvent
    public static void onCheckSpawn(LivingSpawnEvent.CheckSpawn event) {
        if (!(event.getWorld() instanceof Level)) return;
        Level world = event.getEntity().level;

        Entity entity = event.getEntity();
        if (isScared(entity)) {
            if (isScarecrowInRange(entity, world)) event.setResult(Event.Result.DENY);
        }
    }

    //add goal
    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (event.getWorld() == null) return;
        Entity e = event.getEntity();
        if (e instanceof PathfinderMob mob && isScared(e)) {

            mob.goalSelector.addGoal(0, new AvoidEntityGoal<>(mob, TargetDummyEntity.class,
                    Configs.cachedServer.RADIUS, 1.0D, 1.3D, d -> ((TargetDummyEntity) d).isScarecrow()));

        }
    }
}
