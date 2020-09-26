/**
 * This mod element is always locked. Enter your code in the methods below.
 * If you don't need some of these methods, you can remove them as they
 * are overrides of the base class DummmmmmyModElements.ModElement.
 *
 * You can register new events in this class too.
 *
 * As this class is loaded into mod element list, it NEEDS to extend
 * ModElement class. If you remove this extend statement or remove the
 * constructor, the compilation will fail.
 *
 * If you want to make a plain independent class, create it using
 * Project Browser - New... and make sure to make the class
 * outside net.mcreator.dummmmmmy as this package is managed by MCreator.
 *
 * If you change workspace package, modid or prefix, you will need
 * to manually adapt this file to these changes or remake it.
*/
package net.mcreator.dummmmmmy;

import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.dispenser.IDispenseItemBehavior;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraft.block.Block;
import net.minecraft.util.Direction;
import net.minecraft.block.Blocks;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraft.util.IItemProvider;
import net.minecraft.block.DispenserBlock;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.item.HoeItem;
import net.minecraft.item.HoeItem;
import net.minecraft.world.server.ServerWorld;

import net.mcreator.dummmmmmy.item.TargetDummyPlacerItem;

import net.mcreator.dummmmmmy.entity.TargetDummyEntity;

@DummmmmmyModElements.ModElement.Tag
public class DispenserBehavior extends DummmmmmyModElements.ModElement {
	/**
	 * Do not remove this constructor
	 */
	public DispenserBehavior(DummmmmmyModElements instance) {
		super(instance, 8);
	}

	@Override
	public void initElements() {
	}

	@Override
	public void init(FMLCommonSetupEvent event) {
		registerBehaviors();
	}

	@Override
	public void serverLoad(FMLServerStartingEvent event) {
	}




	private static void register(IItemProvider provider, IDispenseItemBehavior behavior) {
		DispenserBlock.registerDispenseBehavior(provider, behavior);
	}



	private static void registerBehaviors() {
		for(Item item : ForgeRegistries.ITEMS) {
			if(item instanceof TargetDummyPlacerItem.ItemCustom){
				register(item, new SpawnDummyBehavior());
			}
		}
	}





	public static class SpawnDummyBehavior implements IDispenseItemBehavior {
	
		@Override
		public ItemStack dispense(IBlockSource dispenser, ItemStack itemStack) {
			if(!(itemStack.getItem() instanceof TargetDummyPlacerItem.ItemCustom)) {
				return itemStack;
			}
	
			World world = dispenser.getWorld();
			Direction direction = dispenser.getBlockState().get(DispenserBlock.FACING);
			BlockPos pos = dispenser.getBlockPos().offset(direction);


			TargetDummyEntity.CustomEntity dummy = new TargetDummyEntity.CustomEntity(world);
			float f = direction.getHorizontalAngle();
			dummy.setLocationAndAngles(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, f, 0.0F);

			//EntityType.applyItemNBT(world, context.getPlayer(), dummy, itemstack.getTag());
			world.addEntity(dummy);

			itemStack.shrink(1);


    		world.playEvent(1000, dispenser.getBlockPos(), 0);


	
			return itemStack;
		}
	
	}
	//PlayerEntity fakePlayer = FakePlayerFactory.getMinecraft((ServerWorld)world);






}
