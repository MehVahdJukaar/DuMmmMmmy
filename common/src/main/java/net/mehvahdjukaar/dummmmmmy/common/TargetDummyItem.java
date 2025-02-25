
package net.mehvahdjukaar.dummmmmmy.common;

import net.mehvahdjukaar.dummmmmmy.Dummmmmmy;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.function.Consumer;

public class TargetDummyItem extends Item {
    public TargetDummyItem(Properties builder) {
        super(builder);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Direction direction = context.getClickedFace();
        if (direction != Direction.DOWN) {
            Level level = context.getLevel();
            BlockPlaceContext placeContext = new BlockPlaceContext(context);
            BlockPos blockpos = placeContext.getClickedPos();
            BlockPos above = blockpos.above();
            if (placeContext.canPlace() && level.getBlockState(above).canBeReplaced(placeContext)) {
                var type = Dummmmmmy.TARGET_DUMMY.get();
                Vec3 vec3 = Vec3.atBottomCenterOf(blockpos);
                AABB aABB = type.getDimensions().makeBoundingBox(vec3.x(), vec3.y(), vec3.z());
                if (level.noCollision(null, aABB) && level.getEntities(null, aABB).isEmpty()) {
                    ItemStack itemstack = context.getItemInHand();
                    if (level instanceof ServerLevel serverLevel) {
                        level.removeBlock(blockpos, false);
                        level.removeBlock(above, false);
                        Consumer<TargetDummyEntity> consumer = EntityType.createDefaultStackConfig(serverLevel, itemstack, context.getPlayer());
                        TargetDummyEntity dummy = new TargetDummyEntity(serverLevel);

                        float rotation = Mth.floor((Mth.wrapDegrees(context.getRotation() - 180.0F) + 11.25) / 22.5F) * 22.5F;
                        dummy.moveTo(vec3.x, vec3.y, vec3.z, rotation, 0.0F);
                        dummy.setYHeadRot(rotation);

                        consumer.accept(dummy);

                        //set custom health
                        if (itemstack.hasCustomHoverName()) {
                            String name = itemstack.getHoverName().getString();
                            try {
                                int i = Integer.parseInt(name);
                                if (i > 0 && i < 10000) {
                                    dummy.getAttribute(Attributes.MAX_HEALTH).setBaseValue(i);
                                }
                            } catch (Exception ignored) {
                            }
                        }

                        level.addFreshEntity(dummy);

                        level.playSound(null, dummy.getX(), dummy.getY(), dummy.getZ(), SoundEvents.BAMBOO_PLACE,
                                SoundSource.BLOCKS, 0.75F, 0.8F);
                        dummy.gameEvent(GameEvent.ENTITY_PLACE, context.getPlayer());
                    }
                    itemstack.shrink(1);
                    return InteractionResult.SUCCESS;
                }
            }

        }
        return InteractionResult.FAIL;
    }
}

