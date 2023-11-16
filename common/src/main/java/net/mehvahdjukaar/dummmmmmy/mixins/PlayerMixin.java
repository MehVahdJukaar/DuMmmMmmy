package net.mehvahdjukaar.dummmmmmy.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.mehvahdjukaar.dummmmmmy.common.ModEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Player.class)
public class PlayerMixin {

    @WrapOperation(method = "actuallyHurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;setHealth(F)V"))
    private void actuallyHurt_setHealth(
            // Mixin parameters
            Player entity, float healthToSet, Operation<Void> original,
            // Context parameters
            DamageSource damageSource, float damageAmount
    ) {
        var originalHealth = entity.getHealth();
        var mitigatedDamageAmount = originalHealth - healthToSet;
        ModEvents.onEntityDamage(entity, mitigatedDamageAmount, damageSource);
        original.call(entity, healthToSet);
    }
}
