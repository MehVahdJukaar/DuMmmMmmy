package net.mehvahdjukaar.dummmmmmy;

import net.mehvahdjukaar.dummmmmmy.common.TargetDummyEntity;
import net.mehvahdjukaar.dummmmmmy.common.TargetDummyItem;
import net.mehvahdjukaar.dummmmmmy.configs.ClientConfigs;
import net.mehvahdjukaar.dummmmmmy.configs.CommonConfigs;
import net.mehvahdjukaar.dummmmmmy.network.NetworkHandler;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.mehvahdjukaar.moonlight.api.platform.RegHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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

    public static void init() {
        if (PlatHelper.getPhysicalSide().isClient()) {
            DummmmmmyClient.init();
            ClientConfigs.init();
        }
        PlatHelper.addCommonSetup(Dummmmmmy::setup);
        CommonConfigs.init();

        RegHelper.addAttributeRegistration(Dummmmmmy::registerEntityAttributes);
        RegHelper.addItemsToTabsRegistration(Dummmmmmy::registerItemsToTab);

        //TODO: damage numbers for other entities
        // TODO: fix max 100 damage??
        // add straw particles
        // make so when you kil the dummy it has a death animation and them respawns after a bit. Optional in configs
    }


    public static void setup() {
        NetworkHandler.registerMessages();

        DispenserBlock.registerBehavior(DUMMY_ITEM.get(), new SpawnDummyBehavior());
    }

    private static void registerItemsToTab(RegHelper.ItemToTabEvent event) {
        event.addBefore(CreativeModeTabs.COMBAT, i -> i.is(Items.TOTEM_OF_UNDYING), DUMMY_ITEM.get());
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
                            //.setTrackingRange(64)
                            //.setUpdateInterval(3)
                            .sized(0.6f, 2f))
                    .build(TARGET_DUMMY_NAME));

    public static final Supplier<Item> DUMMY_ITEM = RegHelper.registerItem(
            res(TARGET_DUMMY_NAME), () -> new TargetDummyItem(new Item.Properties().stacksTo(16)));

    public static final Supplier<SimpleParticleType> NUMBER_PARTICLE = RegHelper.registerParticle(res("number"));


    public static final TagKey<DamageType> IS_THORN = TagKey.create(Registries.DAMAGE_TYPE, res("is_thorn"));

}
