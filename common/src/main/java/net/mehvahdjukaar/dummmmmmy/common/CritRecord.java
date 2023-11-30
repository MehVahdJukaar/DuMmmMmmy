package net.mehvahdjukaar.dummmmmmy.common;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;

public class CritRecord {
    private final Entity critter;
    private final float multiplier;
    private DamageSource source;

    public CritRecord(Entity critter, float mult) {
        this.critter = critter;
        this.multiplier = mult;
    }

    // we don't have this info when crit is generated,
    // yet It's needed to determine to which damage the crit belongs too.
    // we need this as hurt calls can be chained, so just checking a boolean field won't be enough
    public void addSource(DamageSource source) {
        this.source = source;
    }

    public boolean canCompleteWith(DamageSource source) {
        return source != null && (source.getEntity() == critter || source.getDirectEntity() == critter);
    }

    public boolean matches(DamageSource source) {
        return this.source == source;
    }

    public float getMultiplier() {
        return multiplier;
    }
}
