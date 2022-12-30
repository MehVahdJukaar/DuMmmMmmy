package net.mehvahdjukaar.dummmmmmy.common;

import net.mehvahdjukaar.dummmmmmy.Dummmmmmy;
import net.mehvahdjukaar.dummmmmmy.entity.TargetDummyEntity;
import net.mehvahdjukaar.dummmmmmy.setup.ModRegistry;
import net.mehvahdjukaar.moonlight.api.misc.EventCalled;
import net.mehvahdjukaar.moonlight.api.util.Utils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ModEvents {

    @EventCalled
    public static void onEntityCriticalHit(Player entity, Entity target, float damageModifier) {
        if (entity != null) {
            if (damageModifier == 1.5 && target instanceof TargetDummyEntity dummy) {
                dummy.critical = true;
            }
        }
    }

    public static boolean isScared(Entity entity) {
        String name = Utils.getID(entity.getType()).toString();
        return (entity instanceof Animal || CommonConfigs.WHITELIST.get().contains(name))
                && !CommonConfigs.BLACKLIST.get().contains(name);
    }

    public static boolean isScarecrowInRange(Entity entity, Level world) {
        return !world.getEntities(Dummmmmmy.TARGET_DUMMY.get(), entity.getBoundingBox().inflate(10),
                TargetDummyEntity::isScarecrow).isEmpty();
    }

    //prevents them from spawning
    @EventCalled
    public static boolean onCheckSpawn(Mob entity, LevelAccessor level) {
        if (level instanceof Level l) {
            if (isScared(entity)) {
                return isScarecrowInRange(entity, l);
            }
        }
        return false;
    }

    //add goal
    @EventCalled
    public static void onEntityJoinWorld(Entity entity) {
        if (entity instanceof PathfinderMob mob && isScared(entity)) {
            mob.goalSelector.addGoal(0, new AvoidEntityGoal<>(mob, TargetDummyEntity.class,
                   CommonConfigs.RADIUS.get(), 1.0D, 1.3D, d -> ((TargetDummyEntity) d).isScarecrow()));
        }
    }

}
