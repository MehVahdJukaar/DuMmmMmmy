package net.mehvahdjukaar.dummmmmmy.compat;

import net.critical_strike.api.CriticalDamageSource;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.minecraft.world.damagesource.DamageSource;

import java.util.function.Function;

public class CritCompat {
    private static Function<DamageSource, Float> critMultiplierResolver = ds -> 0F;
    public static void init() {
        if (PlatHelper.isModLoaded("critical_strike")) {
            critMultiplierResolver = ds -> ((CriticalDamageSource)ds).rng_getCriticalDamageMultiplier();
        }
    }
    public static float getCritMultiplier(DamageSource damageSource) {
        if (damageSource == null) {
            return 0F;
        }
        return critMultiplierResolver.apply(damageSource);
    }
}
