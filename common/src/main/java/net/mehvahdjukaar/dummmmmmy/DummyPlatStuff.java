package net.mehvahdjukaar.dummmmmmy;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.mehvahdjukaar.dummmmmmy.common.TargetDummyEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class DummyPlatStuff {

    @ExpectPlatform
    public static boolean canDisableShield(LivingEntity attacker, ItemStack useItem, TargetDummyEntity targetDummyEntity) {
        throw new AssertionError();
    }
}
