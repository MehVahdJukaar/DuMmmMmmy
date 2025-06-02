package net.mehvahdjukaar.dummmmmmy.forge;

import net.mehvahdjukaar.dummmmmmy.common.TargetDummyEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class DummyPlatStuffImpl {
    public static boolean canDisableShield(LivingEntity attacker, ItemStack useItem, TargetDummyEntity targetDummyEntity) {
        return attacker.getMainHandItem().canDisableShield(useItem, targetDummyEntity, attacker);
    }
}
