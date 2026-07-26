package net.mehvahdjukaar.dummmmmmy.platform;

import net.mehvahdjukaar.dummmmmmy.common.TargetDummyEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;

public class DummyPlatStuffImpl {

    public static boolean canDisableShield(LivingEntity attacker, ItemStack useItem, TargetDummyEntity targetDummyEntity) {
        return attacker.getMainHandItem().canDisableShield(useItem, targetDummyEntity, attacker);
    }
}
