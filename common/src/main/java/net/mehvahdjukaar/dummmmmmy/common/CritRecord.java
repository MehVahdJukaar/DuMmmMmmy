package net.mehvahdjukaar.dummmmmmy.common;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class CritRecord {
    private final Entity fromEntity;
    private final float multiplier;
    private DamageSource source;

    public CritRecord(Entity fromEntity, float mult) {
        this.fromEntity = fromEntity; //entity that attacked me
        this.multiplier = mult;
    }

    // we don't have this info when crit is generated,
    // yet It's needed to determine to which damage the crit belongs too.
    // we need this as hurt calls can be chained, so just checking a boolean field won't be enough
    public void addSource(@NotNull DamageSource source) {
        this.source = source;
    }

    public boolean canCompleteWith(DamageSource source) {
        return source != null && (source.getEntity() == fromEntity || source.getDirectEntity() == fromEntity);
    }

    public boolean matches(@NotNull DamageSource source) {
        return this.source == source;
    }

    public float getMultiplier() {
        return multiplier;
    }
}
