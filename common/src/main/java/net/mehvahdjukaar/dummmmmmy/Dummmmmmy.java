package net.mehvahdjukaar.dummmmmmy;

import net.mehvahdjukaar.dummmmmmy.common.ClientConfigs;
import net.mehvahdjukaar.dummmmmmy.common.CommonConfigs;
import net.mehvahdjukaar.dummmmmmy.network.NetworkHandler;
import net.mehvahdjukaar.dummmmmmy.entity.DummyNumberEntity;
import net.mehvahdjukaar.dummmmmmy.entity.TargetDummyEntity;
import net.mehvahdjukaar.dummmmmmy.item.TargetDummyItem;
import net.mehvahdjukaar.moonlight.api.platform.RegHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

/**
 * Author: MehVahdJukaar
 */
public class Dummmmmmy {

    public static final String MOD_ID = "dummmmmmy";
    public static final Logger LOGGER = LogManager.getLogger();

    public static ResourceLocation res(String name) {
        return new ResourceLocation(MOD_ID, name);
    }

    public static void commonInit() {
        CommonConfigs.init();
        ClientConfigs.init();

        RegHelper.addAttributeRegistration(Dummmmmmy::registerEntityAttributes);
    }


    public static void commonSetup() {
        NetworkHandler.registerMessages();

        DispenserBlock.registerBehavior(DUMMY_ITEM.get(), new SpawnDummyBehavior());
    }

    private static void registerEntityAttributes(RegHelper.AttributeEvent event) {
        event.register(TARGET_DUMMY.get(), TargetDummyEntity.makeAttributes());
    }

    private static class SpawnDummyBehavior implements DispenseItemBehavior {
        @Override
        public ItemStack dispense(BlockSource dispenser, ItemStack itemStack) {
            Level world = dispenser.getLevel();
            Direction direction = dispenser.getBlockState().getValue(DispenserBlock.FACING);
            BlockPos pos = dispenser.getPos().relative(direction);

            TargetDummyEntity dummy = new TargetDummyEntity(world);
            float f = direction.toYRot();
            dummy.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, f, 0.0F);

            world.addFreshEntity(dummy);
            itemStack.shrink(1);
            world.levelEvent(1000, dispenser.getPos(), 0);
            return itemStack;
        }
    }


    public static final String TARGET_DUMMY_NAME = "target_dummy";
    public static final Supplier<EntityType<TargetDummyEntity>> TARGET_DUMMY = RegHelper.registerEntityType(
            res(TARGET_DUMMY_NAME), () -> (
                    EntityType.Builder.<TargetDummyEntity>of(TargetDummyEntity::new, MobCategory.MISC)
                            //.setShouldReceiveVelocityUpdates(true)
                            //.setTrackingRange(64)
                            //.setCustomClientFactory(TargetDummyEntity::new)
                            //.setUpdateInterval(3)
                            .sized(0.6f, 2f))
                    .build(TARGET_DUMMY_NAME));

    public static final String DUMMY_NUMBER_NAME = "dummy_number";
    public static final Supplier<EntityType<DummyNumberEntity>> DUMMY_NUMBER = RegHelper.registerEntityType(
            res(DUMMY_NUMBER_NAME), () -> (
            EntityType.Builder.<DummyNumberEntity>of(DummyNumberEntity::new, MobCategory.MISC)
                    //.setShouldReceiveVelocityUpdates(true)
                    //.setTrackingRange(64)
                    //.setCustomClientFactory(DummyNumberEntity::new)
                    //.setUpdateInterval(3)
                    .sized(0.6f, 1.8f))
            .build(DUMMY_NUMBER_NAME));


    public static final Supplier<Item> DUMMY_ITEM = RegHelper.registerItem(
            res(TARGET_DUMMY_NAME),
            () -> new TargetDummyItem(
                    new Item.Properties().tab(CreativeModeTab.TAB_COMBAT).stacksTo(16)));
}
