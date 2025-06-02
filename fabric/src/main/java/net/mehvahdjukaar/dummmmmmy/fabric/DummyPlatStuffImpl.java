package net.mehvahdjukaar.dummmmmmy.fabric;

import net.mehvahdjukaar.dummmmmmy.common.TargetDummyEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class DummyPlatStuffImpl {
    public static boolean canDisableShield(LivingEntity attacker, ItemStack useItem, TargetDummyEntity targetDummyEntity) {
        return attacker.canDisableShield();
    }
}
