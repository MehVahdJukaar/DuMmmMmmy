package net.mehvahdjukaar.dummmmmmy.mixins.neoforge;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.mehvahdjukaar.dummmmmmy.common.CritRecord;
import net.mehvahdjukaar.dummmmmmy.common.MeleeCritSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.entity.PartEntity;
import net.neoforged.neoforge.event.entity.player.CriticalHitEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Player.class)
public abstract class PlayerCritMixin {

    @WrapOperation(method = "attack", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Entity;hurtOrSimulate(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    private boolean dummy$rememberCritDuringHurt(Entity target, DamageSource source, float amount, Operation<Boolean> original,
                                             @Local CriticalHitEvent critEvent) {
        if (target.level().isClientSide() || !critEvent.isCriticalHit()) {
            return original.call(target, source, amount);
        }

        Entity victim = target instanceof PartEntity<?> part ? part.getParent() : target;
        var critSource = (MeleeCritSource) source;
        critSource.dummy$setMeleeCrit(new CritRecord(victim, critEvent.getDamageMultiplier()));
        boolean hurt = original.call(target, source, amount);
        critSource.dummy$setMeleeCrit(null);
        return hurt;
    }
}
