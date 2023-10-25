package net.mehvahdjukaar.dummmmmmy.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.mehvahdjukaar.dummmmmmy.common.ModEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @WrapOperation(method = "actuallyHurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;setHealth(F)V"))
    private void actuallyHurt_setHealth(
            // Mixin parameters
            LivingEntity entity, float healthToSet, Operation<Void> original,
            // Context parameters
            DamageSource damageSource, float originalDamageAmount) {
        var originalHealth = entity.getHealth();
        var mitigatedDamageAmount = originalHealth - healthToSet;
        ModEvents.onEntityDamage(entity, mitigatedDamageAmount, damageSource);
        original.call(entity, healthToSet);
    }

    @WrapOperation(method = "heal", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;setHealth(F)V"))
    private void heal_setHealth(
            // Mixin parameters
            LivingEntity entity, float healthToSet, Operation<Void> original,
            // Context parameters
            float originalHealAmount) {
        var originalHealth = entity.getHealth();
        // `actualHealAmount` may be different to `originalHealAmount` if other mods modify healing taken
        var actualHealAmount = healthToSet - originalHealth;
        ModEvents.onEntityHeal(entity, actualHealAmount);
        original.call(entity, healthToSet);
    }
}
