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
    public static final int[] PLAYER_ROTATIONS = new int[ForgeDirection.values().length];

    static
    {
        PLAYER_ROTATIONS[ForgeDirection.UP.ordinal()] = 0;
        PLAYER_ROTATIONS[ForgeDirection.DOWN.ordinal()] = 0;
        PLAYER_ROTATIONS[ForgeDirection.NORTH.ordinal()] = 180;
        PLAYER_ROTATIONS[ForgeDirection.SOUTH.ordinal()] = 0;
        PLAYER_ROTATIONS[ForgeDirection.WEST.ordinal()] = 90;
        PLAYER_ROTATIONS[ForgeDirection.EAST.ordinal()] = -90;
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
            player.rotationYawHead = player.rotationYaw = PLAYER_ROTATIONS[direction.ordinal()];
            System.out.println(player.rotationYaw + ", " + direction.ordinal());
            placing.tryPlaceItemIntoWorld(player, world, x, y, z, direction.getOpposite().ordinal(), 0, 0, 0);
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