package net.mehvahdjukaar.dummmmmmy.compat;

import net.critical_strike.api.CriticalDamageSource;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.minecraft.world.damagesource.DamageSource;

import java.util.function.Function;

public class CritCompat {

    public static float getCritMultiplier(DamageSource damageSource) {
        return ((CriticalDamageSource) damageSource).rng_getCriticalDamageMultiplier();
    }
}
