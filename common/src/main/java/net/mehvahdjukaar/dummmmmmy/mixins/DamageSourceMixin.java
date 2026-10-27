package net.mehvahdjukaar.dummmmmmy.mixins;

import net.mehvahdjukaar.dummmmmmy.common.CritRecord;
import net.mehvahdjukaar.dummmmmmy.common.MeleeCritSource;
import net.minecraft.world.damagesource.DamageSource;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(DamageSource.class)
public class DamageSourceMixin implements MeleeCritSource {


    @Unique
    @Nullable
    private CritRecord dummy$meleeCrit;

    @Override
    public @Nullable CritRecord dummy$getMeleeCrit() {
        return dummy$meleeCrit;
    }

    @Override
    public void dummy$setMeleeCrit(@Nullable CritRecord crit) {
        this.dummy$meleeCrit = crit;
    }
}
