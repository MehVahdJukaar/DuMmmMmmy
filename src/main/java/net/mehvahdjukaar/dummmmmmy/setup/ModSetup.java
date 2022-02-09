package net.mehvahdjukaar.dummmmmmy.setup;

import net.mehvahdjukaar.dummmmmmy.common.NetworkHandler;
import net.mehvahdjukaar.dummmmmmy.entity.TargetDummyEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModSetup {

    public static void init(final FMLCommonSetupEvent event) {

        NetworkHandler.registerMessages();

        DispenserBlock.registerBehavior(ModRegistry.DUMMY_ITEM.get(), new SpawnDummyBehavior());
    }

    @SubscribeEvent
    public static void registerEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(ModRegistry.TARGET_DUMMY.get(), TargetDummyEntity.setCustomAttributes().build());
    }

    public static class SpawnDummyBehavior implements DispenseItemBehavior {
        @Override
        public ItemStack dispense(BlockSource dispenser, ItemStack itemStack) {

            Level world = dispenser.getLevel();
            Direction direction = dispenser.getBlockState().getValue(DispenserBlock.FACING);
            BlockPos pos = dispenser.getPos().relative(direction);

            TargetDummyEntity dummy = new TargetDummyEntity(world);
            float f = direction.toYRot();
            dummy.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, f, 0.0F);

            //EntityType.applyItemNBT(world, context.getPlayer(), dummy, itemstack.getTag());
            world.addFreshEntity(dummy);

            itemStack.shrink(1);

            world.levelEvent(1000, dispenser.getPos(), 0);

            return itemStack;
        }
    }

}
