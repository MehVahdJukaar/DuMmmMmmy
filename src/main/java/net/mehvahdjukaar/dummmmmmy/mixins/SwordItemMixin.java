package net.mehvahdjukaar.dummmmmmy.mixins;

import net.mehvahdjukaar.dummmmmmy.common.Configs;
import net.mehvahdjukaar.dummmmmmy.setup.ModRegistry;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SwordItem.class)
public abstract class SwordItemMixin {

    @Inject(method = "hurtEnemy", at = @At("HEAD"),
            cancellable = true)
    public void hurtItem(ItemStack stack, LivingEntity entity, LivingEntity player, CallbackInfoReturnable<Boolean> cir) {
        if(Configs.cachedServer.DAMAGE_EQUIPMENT && entity.getType() == ModRegistry.TARGET_DUMMY.get()){
            cir.setReturnValue(true);
            cir.cancel();
        }
    }
}
