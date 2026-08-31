package net.mehvahdjukaar.dummmmmmy.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.mehvahdjukaar.dummmmmmy.Dummmmmmy;
import net.mehvahdjukaar.dummmmmmy.configs.CommonConfigs;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @WrapOperation(method = "postHurtEnemy", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/ItemStack;hurtAndBreak(ILnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/EquipmentSlot;)V"))
    private void mm$skipEquipmentDamageOnDummy(ItemStack stack, int amount, LivingEntity attacker, EquipmentSlot slot,
                                               Operation<Void> original, @Local(argsOnly = true, ordinal = 0) LivingEntity mob) {
        if (mob.is(Dummmmmmy.TARGET_DUMMY.get()) && CommonConfigs.DAMAGE_EQUIPMENT.get()) return;
        original.call(stack, amount, attacker, slot);
    }
}
