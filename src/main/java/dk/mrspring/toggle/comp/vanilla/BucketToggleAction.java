package dk.mrspring.toggle.comp.vanilla;

import dk.mrspring.toggle.api.IToggleController;
import dk.mrspring.toggle.api.IToggleStorage;
import dk.mrspring.toggle.tileentity.BasicBlockToggleAction;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStaticLiquid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidBlock;

/**
 * Created by MrSpring on 02-03-2015 for ToggleBlocks.
 */
public class BucketToggleAction extends BasicBlockToggleAction
{
    @Override
    public ItemStack[] harvestBlock(World world, int x, int y, int z, EntityPlayer player, IToggleController controller)
    {
        Block block = world.getBlock(x, y, z);
        System.out.println(block.getClass().getSimpleName());
        if (block instanceof IFluidBlock)
        {
            IToggleStorage storage = controller.getStorageHandler();
            IFluidBlock fluidBlock = (IFluidBlock) block;
            FluidStack containing = fluidBlock.drain(world, x, y, z, true);
            ItemStack emptyContainer = null;
            for (int s = 0; s < storage.getStorageSlots(); s++)
            {
                ItemStack inSlot = storage.getItemFromSlot(s);
                if (FluidContainerRegistry.isEmptyContainer(inSlot))
                {
                    emptyContainer = inSlot;
                    break;
                }
            }
            if (emptyContainer == null) return null;
            ItemStack filledContainer = FluidContainerRegistry.fillFluidContainer(containing, emptyContainer);
            emptyContainer.stackSize--;
            return new ItemStack[]{filledContainer};
        } else if (block instanceof BlockStaticLiquid)
        {
            BlockStaticLiquid liquid = (BlockStaticLiquid) block;
            ItemStack emptyBucket = controller.getStorageHandler().getItemFromStorage(new ItemStack(Items.bucket));
            if (emptyBucket != null)
            {
                emptyBucket.stackSize--;
                ItemStack filledBucket = null;
                if (block == Blocks.water) filledBucket = new ItemStack(Items.water_bucket);
                else if (block == Blocks.lava) filledBucket = new ItemStack(Items.lava_bucket);
                world.setBlockToAir(x, y, z);
                return filledBucket != null ? new ItemStack[]{filledBucket} : null;
            }
        }
        return null;
    }

    @Override
    public void placeBlock(World world, int x, int y, int z, ForgeDirection direction, EntityPlayer player,
                           ItemStack placing, IToggleController controller) // TODO: Add storage item index as parameter?
    {
        if (FluidContainerRegistry.isFilledContainer(placing))
        {
            FluidStack fluidStack = FluidContainerRegistry.getFluidForFilledItem(placing);
            Fluid fluid = fluidStack.getFluid();
            if (fluidStack.getFluid().canBePlacedInWorld())
            {
                Block fluidBlock = fluid.getBlock();
                world.setBlock(x, y, z, fluidBlock);
                world.notifyBlockChange(x, y, z, fluidBlock);
                fluidBlock.onBlockAdded(world, x, y, z);
                ItemStack drainedContainer = FluidContainerRegistry.drainFluidContainer(placing);
                placing.stackSize--;
                controller.getStorageHandler().addItemStackToStorage(drainedContainer);
            }
        } else if (placing.getItem() instanceof ItemBucket)
        {
            ItemBucket item = (ItemBucket) placing.getItem();
            item.tryPlaceContainedLiquid(world, x, y, z);
        }
    }

    @Override
    public boolean canPlaceBlock(World world, int x, int y, int z, ItemStack placing, IToggleController controller)
    {
        return FluidContainerRegistry.isFilledContainer(placing) || placing.getItem() instanceof ItemBucket;
//        return placing.getItem() instanceof ItemBucket && placing.getItem() != Items.bucket;
    }

    @Override
    public boolean canHarvestBlock(World world, int x, int y, int z, IToggleController controller)
    {
        Block block = world.getBlock(x, y, z);
        System.out.println(block.getClass().getSimpleName());
        return (block instanceof IFluidBlock && ((IFluidBlock) block).getFilledPercentage(world, x, y, z) > 0) ||
                block instanceof BlockStaticLiquid;

//        Block block = world.getBlock(x, y, z);
//        return block != null && (block == Blocks.water || block == Blocks.lava);

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
