package net.mehvahdjukaar.dummmmmmy.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.mehvahdjukaar.dummmmmmy.common.TargetDummyEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.enchantment.ConditionalEffect;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;

import java.lang.ref.WeakReference;
import java.util.List;

@Mixin(Enchantment.class)
public abstract class EnchantmentMixin {

    @WrapOperation(method = {"isImmuneToDamage", "runLocationChangedEffects"},
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/ConditionalEffect;matches(Lnet/minecraft/world/level/storage/loot/LootContext;)Z"))
    private boolean dummy$entityAwareMatch(ConditionalEffect<?> instance, LootContext context, Operation<Boolean> operation) {
        boolean original = operation.call(instance, context);
        if (original) return true;

        var entity = context.getOptionalParameter(LootContextParams.THIS_ENTITY);
        if (entity instanceof TargetDummyEntity e) {
            return e.getMobType().isVulnerableTo((Enchantment) (Object) this);

        }
        return false;
    }

    @Unique
    private static WeakReference<Entity> dummy$entityHack = null;
    @Unique
    private static WeakReference<Enchantment> dummy$enchantmentHack = null;

    @ModifyExpressionValue(method = "applyEffects(Ljava/util/List;Lnet/minecraft/world/level/storage/loot/LootContext;Lnet/minecraft/world/item/enchantment/Enchantment$GenericAction;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/ConditionalEffect;matches(Lnet/minecraft/world/level/storage/loot/LootContext;)Z"))
    private static boolean dummy$entityAwareMatch2(boolean original, @Local(argsOnly = true) LootContext context) {
        if (dummy$entityHack != null && dummy$entityHack.get() instanceof TargetDummyEntity te && dummy$enchantmentHack != null) {
            original |= te.getMobType().isVulnerableTo(dummy$enchantmentHack.get());
        }
        dummy$entityHack = null;
        dummy$enchantmentHack = null;
        return original;
    }

    @WrapOperation(method = {"tick", "onProjectileSpawned"},
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/Enchantment;applyEffects(Ljava/util/List;Lnet/minecraft/world/level/storage/loot/LootContext;Lnet/minecraft/world/item/enchantment/Enchantment$GenericAction;)V"))
    private void dummy$stashEntity(List<ConditionalEffect<?>> effects, LootContext context, @Coerce Object action,
                                   Operation<Void> original, @Local(argsOnly = true) Entity entity) {
        dummy$entityHack = new WeakReference<>(entity);
        dummy$enchantmentHack = new WeakReference<>((Enchantment) (Object) this);
        original.call(effects, context, action);
    }

    @WrapOperation(method = {"modifyDamageProtection", "modifyEntityFilteredValue", "modifyDamageFilteredValue"},
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/Enchantment;applyEffects(Ljava/util/List;Lnet/minecraft/world/level/storage/loot/LootContext;Lorg/apache/commons/lang3/mutable/MutableFloat;Lnet/minecraft/world/item/enchantment/Enchantment$FloatAction;)V"))
    private void dummy$stashEntityForValue(List<ConditionalEffect<?>> effects, LootContext context, MutableFloat value, @Coerce Object action,
                                   Operation<Void> original, @Local(argsOnly = true) Entity entity) {
        dummy$entityHack = new WeakReference<>(entity);
        dummy$enchantmentHack = new WeakReference<>((Enchantment) (Object) this);
        original.call(effects, context, value, action);
    }

}
