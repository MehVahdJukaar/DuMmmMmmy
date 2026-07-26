package net.mehvahdjukaar.dummmmmmy;

import net.mehvahdjukaar.candlelight.api.PlatformImpl;
import net.mehvahdjukaar.dummmmmmy.common.TargetDummyEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class DummyPlatStuff {

    @PlatformImpl
    public static boolean canDisableShield(LivingEntity attacker, ItemStack useItem, TargetDummyEntity targetDummyEntity) {
        throw new AssertionError();
    }
}
