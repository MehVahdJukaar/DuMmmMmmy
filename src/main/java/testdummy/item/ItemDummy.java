package testdummy.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import testdummy.entity.EntityDummy;

public class ItemDummy extends Item {
    public ItemDummy() {
        setTranslationKey("dummy");
        setRegistryName("dummy");
        setCreativeTab(CreativeTabs.COMBAT);
    }

    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        if (!worldIn.isRemote) {
            switch (facing.getIndex()) {
                case 0:
                    y--;
                    y--;
                    break;
                case 1:
                    y++;
                    break;
                case 2:
                    z--;
                    break;
                case 3:
                    z++;
                    break;
                case 4:
                    x--;
                    break;
                case 5:
                    x++;
                    break;
            }
            Vec3d playerLookVec = player.getLookVec();
            EntityDummy entity = new EntityDummy(worldIn);
            entity.setPosition(x + 0.5D, y, z + 0.5D);
            entity.setPlacementRotation(playerLookVec, facing.getIndex());
            worldIn.spawnEntity(entity);
            player.getHeldItem(hand).shrink(1);
        }
        return EnumActionResult.SUCCESS;
    }
}
