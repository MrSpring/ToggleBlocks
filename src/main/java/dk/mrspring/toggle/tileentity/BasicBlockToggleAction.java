package dk.mrspring.toggle.tileentity;

import dk.mrspring.toggle.api.IBlockToggleAction;
import dk.mrspring.toggle.api.IToggleController;
import dk.mrspring.toggle.block.BlockBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import java.util.Iterator;
import java.util.List;

/**
 * Created by MrSpring on 02-03-2015 for ToggleBlocks.
 */
public class BasicBlockToggleAction implements IBlockToggleAction
{
    public static final int[] PLAYER_ROTATIONS = new int[EnumFacing.values().length];

    static
    {
        PLAYER_ROTATIONS[EnumFacing.UP.ordinal()] = 0;
        PLAYER_ROTATIONS[EnumFacing.DOWN.ordinal()] = 0;
        PLAYER_ROTATIONS[EnumFacing.NORTH.ordinal()] = 180;
        PLAYER_ROTATIONS[EnumFacing.SOUTH.ordinal()] = 0;
        PLAYER_ROTATIONS[EnumFacing.WEST.ordinal()] = 90;
        PLAYER_ROTATIONS[EnumFacing.EAST.ordinal()] = -90;
    }

    @Override
    public ItemStack[] harvestBlock(World world, BlockPos pos, EntityPlayer player,
                                    IToggleController controller)
    {
        IBlockState state = world.getBlockState(pos);
        List<ItemStack> drops = state.getBlock().getDrops(world, pos, state, 0);
        for (Iterator<ItemStack> iterator = drops.iterator(); iterator.hasNext(); )
        {
            ItemStack stack = iterator.next();
            if (stack != null)
                if (stack.isItemEqual(new ItemStack(BlockBase.change_block)))
                    iterator.remove();
        }
        ItemStack[] items = drops.toArray(new ItemStack[drops.size()]);
        world.setBlockToAir(pos);
        return items;
    }

    @Override
    public void placeBlock(World world, BlockPos pos, EnumFacing direction, EntityPlayer player,
                           ItemStack placing, IToggleController tileEntity)
    {
        if (placing != null)
        {
            player.setItemInUse(placing, 0);
            player.rotationYawHead = player.rotationYaw = PLAYER_ROTATIONS[direction.ordinal()];
            System.out.println(player.rotationYaw + ", " + direction.ordinal());
            tryPlaceItemIntoWorld(placing, player, world, pos, direction.getOpposite(), 0, 0, 0);
        }
    }

    /**
     * Copied method ItemStack#tryPlaceItemIntoWorld from 1.7
     */
    public static boolean tryPlaceItemIntoWorld(ItemStack stack, EntityPlayer player, World world, BlockPos pos,
                                                EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (!world.isRemote)
            return net.minecraftforge.common.ForgeHooks.onPlaceItemIntoWorld(stack, player, world, pos, side, hitX, hitY, hitZ);
        boolean flag = stack.getItem().onItemUse(stack, player, world, pos, side, hitX, hitY, hitZ);
        if (flag) player.addStat(StatList.objectUseStats[Item.getIdFromItem(stack.getItem())], 1);
        return flag;
    }

    @Override
    public boolean canPlaceBlock(World world, BlockPos pos, ItemStack placing, IToggleController tileEntity)
    {
        return true;
    }

    @Override
    public boolean canHarvestBlock(World world, BlockPos pos, IToggleController controller)
    {
        return true;
    }
}