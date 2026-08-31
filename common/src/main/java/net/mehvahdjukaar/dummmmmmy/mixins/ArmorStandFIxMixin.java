package net.mehvahdjukaar.dummmmmmy.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.ArmorStandItem;
import net.minecraft.world.item.context.UseOnContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ArmorStandItem.class)
public abstract class ArmorStandFIxMixin {

    @Inject(method = "useOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/decoration/ArmorStand;snapTo(DDDFF)V",
    shift = At.Shift.AFTER))
    public void dummy$fixInitialArmorStandRot(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir,
                                              @Local ArmorStand stand) {
        stand.setYHeadRot(stand.getYRot());
    }
}
