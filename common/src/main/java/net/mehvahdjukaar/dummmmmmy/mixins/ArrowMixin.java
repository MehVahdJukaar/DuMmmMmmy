package net.mehvahdjukaar.dummmmmmy.mixins;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Arrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Arrow.class)
public abstract class ArrowMixin {

    @Inject(method ="doPostHurtEffects", at = @At("HEAD"))
    protected void allowEffectsToHurt(LivingEntity target, CallbackInfo ci,
                                      @Share("lastInvTimer")LocalIntRef lastInvTimer) {
        lastInvTimer.set(target.invulnerableTime);
        target.invulnerableTime = 0;
    }

    @Inject(method ="doPostHurtEffects", at = @At("RETURN"))
    protected void resetInvTimer(LivingEntity target, CallbackInfo ci,
                                      @Share("lastInvTimer")LocalIntRef lastInvTimer) {


        target.invulnerableTime = Math.max(target.invulnerableTime, lastInvTimer.get());
    }
}
