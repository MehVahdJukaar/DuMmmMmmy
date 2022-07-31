package net.mehvahdjukaar.dummmmmmy.setup;

import net.mehvahdjukaar.dummmmmmy.DummmmmmyMod;
import net.mehvahdjukaar.dummmmmmy.entity.DummyNumberEntity;
import net.mehvahdjukaar.dummmmmmy.entity.TargetDummyEntity;
import net.mehvahdjukaar.dummmmmmy.item.TargetDummyItem;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@SuppressWarnings("unused")
public class ModRegistry {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, DummmmmmyMod.MOD_ID);
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, DummmmmmyMod.MOD_ID);

    public static void init(IEventBus bus) {
        ITEMS.register(bus);
        ENTITIES.register(bus);
    }


    public static final String TARGET_DUMMY_NAME = "target_dummy";
    public static final RegistryObject<EntityType<TargetDummyEntity>> TARGET_DUMMY = ENTITIES.register(TARGET_DUMMY_NAME, ()->(
            EntityType.Builder.<TargetDummyEntity>of(TargetDummyEntity::new, MobCategory.MISC)
                    .setShouldReceiveVelocityUpdates(true)
                    .setTrackingRange(64)
                    .setCustomClientFactory(TargetDummyEntity::new)
                    //.setUpdateInterval(3)
                    .sized(0.6f, 2f))
            .build(TARGET_DUMMY_NAME));

    public static final String DUMMY_NUMBER_NAME = "dummy_number";
    public static final RegistryObject<EntityType<DummyNumberEntity>> DUMMY_NUMBER = ENTITIES.register(DUMMY_NUMBER_NAME, ()->(
            EntityType.Builder.<DummyNumberEntity>of(DummyNumberEntity::new, MobCategory.MISC)
                    .setShouldReceiveVelocityUpdates(true)
                    .setTrackingRange(64)
                    .setCustomClientFactory(DummyNumberEntity::new)
                    //.setUpdateInterval(3)
                    .sized(0.6f, 1.8f))
            .build(DUMMY_NUMBER_NAME));

    public static final String DUMMY_ITEM_NAME = "target_dummy_placer";
    public static final RegistryObject<Item> DUMMY_ITEM = ITEMS.register(DUMMY_ITEM_NAME, ()-> new TargetDummyItem(
            new Item.Properties().tab(CreativeModeTab.TAB_COMBAT).stacksTo(16)));


}