
package net.mcreator.dummmmmmy.item;

import net.minecraftforge.registries.ObjectHolder;

import net.minecraft.world.World;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.Direction;
import net.minecraft.util.ActionResultType;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Item;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Entity;
import net.minecraft.block.BlockState;

import net.mcreator.dummmmmmy.entity.TargetDummyEntity;
import net.mcreator.dummmmmmy.DummmmmmyModElements;


import java.util.List;

@DummmmmmyModElements.ModElement.Tag
public class TargetDummyPlacerItem extends DummmmmmyModElements.ModElement {
	@ObjectHolder("dummmmmmy:target_dummy_placer")
	public static final Item block = null;
	public TargetDummyPlacerItem(DummmmmmyModElements instance) {
		super(instance, 3);
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new ItemCustom());
	}
	public static class ItemCustom extends Item {
		public ItemCustom() {
			super(new Item.Properties().group(ItemGroup.COMBAT).maxStackSize(16));
			setRegistryName("target_dummy_placer");
		}

		@Override
		public int getItemEnchantability() {
			return 0;
		}

		@Override
		public int getUseDuration(ItemStack itemstack) {
			return 0;
		}

		@Override
		public float getDestroySpeed(ItemStack par1ItemStack, BlockState par2Block) {
			return 1F;
		}

		@Override
		public ActionResultType onItemUse(ItemUseContext context) {
			Direction direction = context.getFace();
			if (direction == Direction.DOWN) {
				return ActionResultType.FAIL;
			} else {
				World world = context.getWorld();
				BlockItemUseContext blockitemusecontext = new BlockItemUseContext(context);
				BlockPos blockpos = blockitemusecontext.getPos();
				BlockPos blockpos1 = blockpos.up();
				if (blockitemusecontext.canPlace() && world.getBlockState(blockpos1).isReplaceable(blockitemusecontext)) {
					double d0 = (double) blockpos.getX();
					double d1 = (double) blockpos.getY();
					double d2 = (double) blockpos.getZ();
					List<Entity> list = world.getEntitiesWithinAABBExcludingEntity((Entity) null,
							new AxisAlignedBB(d0, d1, d2, d0 + 1.0D, d1 + 2.0D, d2 + 1.0D));
					if (!list.isEmpty()) {
						return ActionResultType.FAIL;
					} else {
						ItemStack itemstack = context.getItem();
						if (!world.isRemote) {
							world.removeBlock(blockpos, false);
							world.removeBlock(blockpos1, false);
							TargetDummyEntity.CustomEntity dummy = new TargetDummyEntity.CustomEntity(world);
							float f = (float) MathHelper.floor((MathHelper.wrapDegrees(context.getPlacementYaw() - 180.0F) + 11.25) / 22.5F) * 22.5F;
							dummy.setLocationAndAngles(d0 + 0.5D, d1, d2 + 0.5D, f, 0.0F);
							EntityType.applyItemNBT(world, context.getPlayer(), dummy, itemstack.getTag());
							world.addEntity(dummy);
							world.playSound((PlayerEntity) null, dummy.getPosX(), dummy.getPosY(), dummy.getPosZ(), SoundEvents.BLOCK_BAMBOO_PLACE,
									SoundCategory.BLOCKS, 0.75F, 0.8F);
						}
						itemstack.shrink(1);
						return ActionResultType.SUCCESS;
					}
				} else {
					return ActionResultType.FAIL;
				}
			}
		}
	}
}
