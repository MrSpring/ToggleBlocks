package dk.mrspring.toggle.tileentity;

import dk.mrspring.toggle.api.IBlockToggleAction;
import dk.mrspring.toggle.api.IToggleController;
import dk.mrspring.toggle.block.BlockBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.Iterator;
import java.util.List;

/**
 * Created by MrSpring on 02-03-2015 for ToggleBlocks.
 */
public class BasicBlockToggleAction implements IBlockToggleAction
{
    public static int[] translateForDirection(ForgeDirection direction, int x, int y, int z)
    {
        int[] pos = new int[]{x, y, z};
        pos[0] += direction.offsetX;
        pos[1] += direction.offsetY;
        pos[2] += direction.offsetZ;
        return pos;
    }

    public static int[] translateOppositeForDirection(ForgeDirection direction, int x, int y, int z)
    {
        int[] pos = new int[]{x, y, z};
        pos[0] -= direction.offsetX;
        pos[1] -= direction.offsetY;
        pos[2] -= direction.offsetZ;
        return pos;
    }

    @Override
    public ItemStack[] harvestBlock(World world, int x, int y, int z, EntityPlayer player,
                                    IToggleController controller)
    {
        int metadata = world.getBlockMetadata(x, y, z);
        List<ItemStack> drops = world.getBlock(x, y, z).getDrops(world, x, y, z, metadata, 0);
        for (Iterator<ItemStack> iterator = drops.iterator(); iterator.hasNext(); )
        {
            ItemStack stack = iterator.next();
            if (stack != null)
                if (stack.isItemEqual(new ItemStack(BlockBase.change_block)))
                    iterator.remove();
        }
        ItemStack[] items = drops.toArray(new ItemStack[drops.size()]);
        world.setBlockToAir(x, y, z);
        return items;
    }

    @Override
    public void placeBlock(World world, int x, int y, int z, ForgeDirection direction, EntityPlayer player,
                           ItemStack placing, IToggleController tileEntity)
    {
        if (placing != null)
        {
            player.setItemInUse(placing, 0);
            int[] placingPos = translateForDirection(direction, x, y, z);
            placing.tryPlaceItemIntoWorld(player, world, placingPos[0], placingPos[1], placingPos[2],
                    direction.getOpposite().ordinal(), 0, 0, 0);
        }
    }

    @Override
    public boolean canPlaceBlock(World world, int x, int y, int z, ItemStack placing, IToggleController tileEntity)
    {
        return true;
    }

    @Override
    public boolean canHarvestBlock(World world, int x, int y, int z, IToggleController controller)
    {
        return true;
    }
}