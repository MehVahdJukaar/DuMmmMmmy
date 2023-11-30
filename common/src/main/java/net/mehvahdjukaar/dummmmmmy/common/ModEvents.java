package net.mehvahdjukaar.dummmmmmy.common;

import net.mehvahdjukaar.dummmmmmy.Dummmmmmy;
import net.mehvahdjukaar.dummmmmmy.configs.CommonConfigs;
import net.mehvahdjukaar.dummmmmmy.network.ClientBoundDamageNumberMessage;
import net.mehvahdjukaar.dummmmmmy.network.NetworkHandler;
import net.mehvahdjukaar.moonlight.api.misc.EventCalled;
import net.mehvahdjukaar.moonlight.api.util.Utils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

public class ModEvents {

    @EventCalled
    public static void onEntityCriticalHit(Player attacker, Entity target, float damageModifier) {
        if (attacker != null && !attacker.level().isClientSide) { // && damageModifier == 1.5
            if (target instanceof TargetDummyEntity dummy) {
                dummy.moist(attacker, damageModifier);
            }
        }
    }

    public static boolean canBeScaredByScarecrow(Entity entity) {
        String name = Utils.getID(entity.getType()).toString();
        return (entity instanceof Animal || CommonConfigs.WHITELIST.get().contains(name))
                && !CommonConfigs.BLACKLIST.get().contains(name);
    }

    public static boolean isScarecrowInRange(Entity entity, Level world) {
        return !world.getEntities(Dummmmmmy.TARGET_DUMMY.get(), entity.getBoundingBox().inflate(10),
                TargetDummyEntity::canScare).isEmpty();
    }

    //prevents them from spawning
    @EventCalled
    public static boolean onCheckSpawn(Mob entity, LevelAccessor level) {
        if (level instanceof Level l) {
            if (canBeScaredByScarecrow(entity)) {
                return isScarecrowInRange(entity, l);
            }
        }
        return false;
    }

    //add goal
    @EventCalled
    public static void onEntityJoinWorld(Entity entity) {
        if (entity instanceof PathfinderMob mob && canBeScaredByScarecrow(entity)) {
            mob.goalSelector.addGoal(0, new AvoidEntityGoal<>(mob, TargetDummyEntity.class,
                    CommonConfigs.RADIUS.get(), 1.0D, 1.3D, d -> ((TargetDummyEntity) d).canScare()));
        }
        if (CommonConfigs.DECOY.get() && entity instanceof Monster m) {
            m.goalSelector.addGoal(6, new NearestAttackableTargetGoal<>(m, TargetDummyEntity.class,
                    20, true, true, d -> ((TargetDummyEntity) d).canAttract()));
        }
    }

    @EventCalled
    public static void onEntityDamage(LivingEntity target, float amount, DamageSource source) {
        //this should be client sided buuut its only fired on server
        if (!target.level().isClientSide && target.getType() != Dummmmmmy.TARGET_DUMMY.get()) {
            var message = new ClientBoundDamageNumberMessage(target.getId(), amount, source, null);
            switch (CommonConfigs.DAMAGE_NUMBERS_MODE.get()) {
                case ALL_ENTITIES -> {
                    NetworkHandler.CHANNEL.sentToAllClientPlayersTrackingEntity(target, message);
                }
                case ALL_PLAYERS -> {
                    if (source.getEntity() instanceof ServerPlayer) {
                        NetworkHandler.CHANNEL.sentToAllClientPlayersTrackingEntity(target, message);
                    }
                }
                case LOCAL_PLAYER -> {
                    if (source.getEntity() instanceof ServerPlayer attackingPlayer) {
                        NetworkHandler.CHANNEL.sendToClientPlayer(attackingPlayer, message);
                    }
               }
               case NONE -> {
                    // Nothing to do :)
               }
            }
        }
    }

    public static void onEntityHeal(LivingEntity entity, float amount) {
        if (!entity.level().isClientSide && entity.getType() != Dummmmmmy.TARGET_DUMMY.get()) {
            var message = new ClientBoundDamageNumberMessage(entity.getId(), -amount, null, null);
            switch (CommonConfigs.HEALING_NUMBERS_MODE.get()) {
                case ALL_ENTITIES -> {
                    NetworkHandler.CHANNEL.sentToAllClientPlayersTrackingEntity(entity, message);
                }
                case ALL_PLAYERS -> {
                    if (entity instanceof ServerPlayer) {
                        NetworkHandler.CHANNEL.sentToAllClientPlayersTrackingEntity(entity, message);
                    }
                }
                case LOCAL_PLAYER -> {
                    if (entity instanceof ServerPlayer player) {
                        NetworkHandler.CHANNEL.sendToClientPlayer(player, message);
                    }
                }
                case NONE -> {
                    // Nothing to do :)
                }
            }
        }
    }
}
