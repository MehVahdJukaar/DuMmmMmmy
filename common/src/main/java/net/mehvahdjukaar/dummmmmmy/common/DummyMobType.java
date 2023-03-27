package net.mehvahdjukaar.dummmmmmy.common;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CarvedPumpkinBlock;

public enum DummyMobType {
    UNDEFINED(MobType.UNDEFINED),
    UNDEAD(MobType.UNDEAD),
    WATER(MobType.WATER),
    ILLAGER(MobType.ILLAGER),
    ARTHROPOD(MobType.ARTHROPOD),
    SCARECROW(MobType.UNDEFINED),
    DECOY(MobType.UNDEFINED);
    private final MobType type;

    DummyMobType(MobType type) {
        this.type = type;
    }

    public MobType getType() {
        return type;
    }

    public static DummyMobType get(ItemStack headStack) {
        if (isUndeadSkull(headStack)) return UNDEAD;
        else if (headStack.is(Items.TURTLE_HELMET)) return WATER;
        else if (headStack.is(Items.DRAGON_HEAD)) return ARTHROPOD;
        else if (headStack.is(Items.PLAYER_HEAD)) return DECOY;
        else if (ItemStack.matches(headStack, Raid.getLeaderBannerInstance())) return ILLAGER;
        else if (isPumpkin(headStack.getItem())) return SCARECROW;
        else return UNDEFINED;
    }

    private static boolean isPumpkin(Item item) {
        if (item instanceof BlockItem bi) {
            Block block = bi.getBlock();
            String name = BuiltInRegistries.ITEM.getKey(item).getPath();
            return block instanceof CarvedPumpkinBlock || name.contains("pumpkin") || name.contains("jack_o");
        }
        return false;
    }

    private static boolean isUndeadSkull(ItemStack itemstack) {
        Item i = itemstack.getItem();
        return i == Items.WITHER_SKELETON_SKULL ||
                i == Items.SKELETON_SKULL ||
                i == Items.ZOMBIE_HEAD;
    }


}
