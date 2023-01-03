package net.mehvahdjukaar.dummmmmmy.common;

import net.mehvahdjukaar.dummmmmmy.configs.ClientConfigs;
import net.minecraft.world.damagesource.DamageSource;

public enum DamageType {
    GENERIC,
    CRIT,
    DRAGON,
    WITHER,
    EXPLOSION,
    MAGIC,
    IND_MAGIC,
    TRIDENT,
    FIRE,
    LIGHTNING,
    CACTUS,
    TRUE;

    //only client
    public int getColor() {
        return switch (this) {
            case CRIT -> ClientConfigs.DAMAGE_CRIT.get();
            case FIRE -> ClientConfigs.DAMAGE_FIRE.get();
            case MAGIC -> ClientConfigs.DAMAGE_MAGIC.get();
            case CACTUS -> ClientConfigs.DAMAGE_CACTUS.get();
            case DRAGON -> ClientConfigs.DAMAGE_DRAGON.get();
            case WITHER -> ClientConfigs.DAMAGE_WITHER.get();
            case GENERIC -> ClientConfigs.DAMAGE_GENERIC.get();
            case TRIDENT -> ClientConfigs.DAMAGE_TRIDENT.get();
            case EXPLOSION -> ClientConfigs.DAMAGE_EXPLOSION.get();
            case IND_MAGIC -> ClientConfigs.DAMAGE_IND_MAGIC.get();
            case LIGHTNING -> ClientConfigs.DAMAGE_LIGHTNING.get();
            case TRUE -> ClientConfigs.DAMAGE_TRUE.get();
        };
    }

    public static DamageType get(DamageSource source, boolean critical) {
        if (source == null) return DamageType.TRUE;
        if (critical) return DamageType.CRIT;
        if (source == DamageSource.DRAGON_BREATH) return DamageType.DRAGON;
        if (source == DamageSource.WITHER) return DamageType.WITHER;
        if (source.msgId.equals("explosion") || source.msgId.equals("explosion.player") || source.isExplosion())
            return DamageType.EXPLOSION;
        if (source.msgId.equals("indirectMagic")) return DamageType.IND_MAGIC;
        if (source.msgId.equals("trident")) return DamageType.TRIDENT;
        if (source == DamageSource.HOT_FLOOR || source == DamageSource.LAVA || source == DamageSource.ON_FIRE
                || source == DamageSource.IN_FIRE || source.isFire()) return DamageType.FIRE;
        if (source == DamageSource.MAGIC || source.isMagic()) {
            //would really like to detect poison damage, but I don't think there's a simple way
            return DamageType.MAGIC;
        }
        if (source == DamageSource.LIGHTNING_BOLT) return DamageType.LIGHTNING;

        if (source == DamageSource.CACTUS || source == DamageSource.SWEET_BERRY_BUSH) return DamageType.CACTUS;
        return DamageType.GENERIC;
    }
}
