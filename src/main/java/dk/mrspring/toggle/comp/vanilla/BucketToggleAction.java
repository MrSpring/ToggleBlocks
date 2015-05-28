package dk.mrspring.toggle.comp.vanilla;

import dk.mrspring.toggle.api.IToggleController;
import dk.mrspring.toggle.tileentity.BasicBlockToggleAction;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Created by MrSpring on 02-03-2015 for ToggleBlocks.
 */
public class BucketToggleAction extends BasicBlockToggleAction // TODO: FML Fluid support
{
    @Override
    public ItemStack[] harvestBlock(World world, int x, int y, int z, EntityPlayer player, IToggleController controller)
    {
        System.out.println("Harvesting");
        ItemStack emptyBucket = controller.getStorageHandler().getItemFromStorage(new ItemStack(Items.bucket));
        if (emptyBucket != null)
        {
            Block block = world.getBlock(x, y, z);
            if (block != null && (block == Blocks.water || block == Blocks.lava))
            {
                emptyBucket.stackSize--;
                ItemStack filledBucket;
                if (block == Blocks.water)
                    filledBucket = new ItemStack(Items.water_bucket);
                else filledBucket = new ItemStack(Items.lava_bucket);
                world.setBlockToAir(x, y, z);
                System.out.println("Returning");
                return new ItemStack[]{filledBucket};
            }
            System.out.println("Block was null or not water/lava");
        }
        System.out.println("Bucket stack was null");
        return null;
        /*Block block = world.getBlock(x, y, z);
        Fluid fromBlock = FluidRegistry.lookupFluidForBlock(block);
        if (fromBlock == null)
            return null;
        ItemStack[] storage = controller.getAllStorage();
        ItemStack emptyContainer = null;
        for (ItemStack stack : storage)
            if (stack != null)
                if (FluidContainerRegistry.isEmptyContainer(stack))
                    if (FluidContainerRegistry.fillFluidContainer(new FluidStack(fromBlock, 1000), stack) != null)
                        emptyContainer = stack;
        if (emptyContainer != null)
        {
            FluidStack fluidStack = new FluidStack(fromBlock, 1000);
            ItemStack filledContainer = FluidContainerRegistry.fillFluidContainer(fluidStack, emptyContainer);
            if (filledContainer != null)
            {
//                controller.removeItemFromStorage(emptyContainer);
                emptyContainer.stackSize--;
                world.setBlockToAir(x, y, z);
                return new ItemStack[]{filledContainer};
            }
        }
        return null;*/
    }

    @Override
    public void placeBlock(World world, int x, int y, int z, ForgeDirection direction, EntityPlayer player,
                           ItemStack placing, IToggleController controller)
    {
//        System.out.println("Placing block");
//        placing.tryPlaceItemIntoWorld(player, world, x, y-1, z, ForgeDirection.UP.ordinal(), 0, 0, 0);
//        if (FluidContainerRegistry.isFilledContainer(placing))
//        {
//            FluidStack fluidStack = FluidContainerRegistry.getFluidForFilledItem(placing);
//            Fluid fluid = fluidStack.getFluid();
//            if (fluidStack.getFluid().canBePlacedInWorld())
//            {
//                Block fluidBlock = fluid.getBlock();
//                world.setBlock(x, y, z, fluidBlock);
//                world.notifyBlockChange(x,y,z,fluidBlock);
//                fluidBlock.onBlockAdded(world, x, y, z);
//                ItemStack drainedContainer = FluidContainerRegistry.drainFluidContainer(placing);
//                placing.stackSize--;
//                controller.addItemStackToStorage(drainedContainer);
//            }
//        }

        if (((ItemBucket) placing.getItem()).tryPlaceContainedLiquid(world, x, y, z))
        {
            placing.stackSize--;
            controller.getStorageHandler().validateStorage();
            controller.getStorageHandler().addItemStackToStorage(new ItemStack(Items.bucket));
        }
    }

    @Override
    public boolean canPlaceBlock(World world, int x, int y, int z, ItemStack placing, IToggleController controller)
    {
//        return FluidContainerRegistry.isFilledContainer(placing);
        return placing.getItem() instanceof ItemBucket && placing.getItem() != Items.bucket;
    }

    @Override
    public boolean canHarvestBlock(World world, int x, int y, int z, IToggleController controller)
    {
        Block block = world.getBlock(x, y, z);
        return block != null && (block == Blocks.water || block == Blocks.lava);

        /*Block block = world.getBlock(x, y, z);
        if (block == null)
            return false;
        Fluid fromBlock = FluidRegistry.lookupFluidForBlock(block);
        if (fromBlock == null)
            return false;
        if (!FluidRegistry.isFluidRegistered(fromBlock))
            return false;
        ItemStack[] storage = controller.getAllStorage();
        ItemStack fluidContainer = null;
        for (ItemStack stack : storage)
            if (stack != null)
                if (FluidContainerRegistry.isEmptyContainer(stack))
                    if (FluidContainerRegistry.fillFluidContainer(new FluidStack(fromBlock, 1000), stack) != null)
                        fluidContainer = stack.copy();
        return fluidContainer != null;*/
    }
}
