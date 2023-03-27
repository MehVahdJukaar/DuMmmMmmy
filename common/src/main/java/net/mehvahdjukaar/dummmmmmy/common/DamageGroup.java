package net.mehvahdjukaar.dummmmmmy.common;

import net.mehvahdjukaar.dummmmmmy.Dummmmmmy;
import net.mehvahdjukaar.dummmmmmy.configs.ClientConfigs;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.level.Level;

public enum DamageGroup {
    GENERIC,
    CRIT,
    DRAGON,
    WITHER,
    EXPLOSION,
    FREEZING,
    MAGIC,
    TRIDENT,
    FIRE,
    LIGHTNING,
    THORN,
    TRUE;

    //only client
    public int getColor() {
        return switch (this) {
            case CRIT -> ClientConfigs.DAMAGE_CRIT.get();
            case FIRE -> ClientConfigs.DAMAGE_FIRE.get();
            case FREEZING -> ClientConfigs.DAMAGE_FREEZING.get();
            case THORN -> ClientConfigs.DAMAGE_CACTUS.get();
            case DRAGON -> ClientConfigs.DAMAGE_DRAGON.get();
            case WITHER -> ClientConfigs.DAMAGE_WITHER.get();
            case GENERIC -> ClientConfigs.DAMAGE_GENERIC.get();
            case TRIDENT -> ClientConfigs.DAMAGE_TRIDENT.get();
            case EXPLOSION -> ClientConfigs.DAMAGE_EXPLOSION.get();
            case MAGIC -> ClientConfigs.DAMAGE_MAGIC.get();
            case LIGHTNING -> ClientConfigs.DAMAGE_LIGHTNING.get();
            case TRUE -> ClientConfigs.DAMAGE_TRUE.get();
        };
    }

    public static DamageGroup get(DamageSource source, Level level, boolean critical) {
        DamageSources sources = level.damageSources();
        if (source == null) return DamageGroup.TRUE;
        if (critical) return DamageGroup.CRIT;
        if (source.is(Dummmmmmy.IS_THORN)) return DamageGroup.THORN;
        if (source == sources.dragonBreath()) return DamageGroup.DRAGON;
        if (source == sources.wither()) return DamageGroup.WITHER;
        if (source.is(DamageTypeTags.IS_EXPLOSION)) return DamageGroup.EXPLOSION;
        if (source.getMsgId().equals("trident")) return DamageGroup.TRIDENT;
        if (source.is(DamageTypeTags.IS_FIRE)) return DamageGroup.FIRE;
        if (source.is(DamageTypeTags.WITCH_RESISTANT_TO)) {
            //would really like to detect poison damage, but I don't think there's a simple way
            return DamageGroup.MAGIC;
        }
        if (source.is(DamageTypeTags.IS_LIGHTNING)) return DamageGroup.LIGHTNING;
        return DamageGroup.GENERIC;
    }
}
