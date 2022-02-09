
package net.mehvahdjukaar.dummmmmmy.item;

import net.mehvahdjukaar.dummmmmmy.entity.TargetDummyEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class TargetDummyItem extends Item {
	public TargetDummyItem(Properties builder) {
		super(builder);
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		Direction direction = context.getClickedFace();
		if (direction == Direction.DOWN) {
			return InteractionResult.FAIL;
		} else {
			Level world = context.getLevel();
			BlockPlaceContext blockitemusecontext = new BlockPlaceContext(context);
			BlockPos blockpos = blockitemusecontext.getClickedPos();
			BlockPos blockpos1 = blockpos.above();
			if (blockitemusecontext.canPlace() && world.getBlockState(blockpos1).canBeReplaced(blockitemusecontext)) {
				double d0 = blockpos.getX();
				double d1 = blockpos.getY();
				double d2 = blockpos.getZ();
				List<Entity> list = world.getEntities((Entity) null,
						new AABB(d0, d1, d2, d0 + 1.0D, d1 + 2.0D, d2 + 1.0D));
				if (!list.isEmpty()) {
					return InteractionResult.FAIL;
				} else {
					ItemStack itemstack = context.getItemInHand();
					if (!world.isClientSide) {
						world.removeBlock(blockpos, false);
						world.removeBlock(blockpos1, false);
						TargetDummyEntity dummy = new TargetDummyEntity(world);
						float f = (float) Mth.floor((Mth.wrapDegrees(context.getRotation() - 180.0F) + 11.25) / 22.5F) * 22.5F;
						dummy.moveTo(d0 + 0.5D, d1, d2 + 0.5D, f, 0.0F);
						EntityType.updateCustomEntityTag(world, context.getPlayer(), dummy, itemstack.getTag());
						world.addFreshEntity(dummy);
						world.playSound(null, dummy.getX(), dummy.getY(), dummy.getZ(), SoundEvents.BAMBOO_PLACE,
								SoundSource.BLOCKS, 0.75F, 0.8F);
					}
					itemstack.shrink(1);
					return InteractionResult.SUCCESS;
				}
			} else {
				return InteractionResult.FAIL;
			}
		}
	}
}

