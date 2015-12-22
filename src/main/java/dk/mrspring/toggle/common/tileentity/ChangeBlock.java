package dk.mrspring.toggle.common.tileentity;

import dk.mrspring.toggle.api.IBlockToggleAction;
import dk.mrspring.toggle.api.IToggleController;
import dk.mrspring.toggle.common.api_impl.ToggleActionRegistry;
import dk.mrspring.toggle.common.block.BlockBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import java.util.List;

/**
 * Created on 12-12-2015 for ToggleBlocks.
 */
public class ChangeBlock
{
    public static final IBlockToggleAction FALLBACK_ACTION = new BasicBlockToggleAction();

    BlockPos pos;
    StateOverride[] overrides;
    EnumFacing direction;

    public ChangeBlock(BlockPos pos, EnumFacing direction, IToggleController controller)
    {
        this.pos = pos;
        this.overrides = new StateOverride[controller.getStateCount()];
        this.direction = direction;
    }

    public ChangeBlock(NBTTagCompound compound, IToggleController controller)
    {
        this.readFromNBT(compound, controller);
    }

    public void writeToNBT(NBTTagCompound compound)
    {
        compound.setInteger("X", pos.getX());
        compound.setInteger("Y", pos.getY());
        compound.setInteger("Z", pos.getZ());
        NBTTagList overrideList = new NBTTagList();
        for (int i = 0; i < overrides.length; i++)
        {
            if (overrides[i] == null) continue;
            NBTTagCompound stateCompound = new NBTTagCompound();
            overrides[i].writeToNBT(stateCompound);
            stateCompound.setInteger("State", i);
            overrideList.appendTag(stateCompound);
        }
        compound.setTag("OverrideList", overrideList);
        compound.setInteger("Direction", direction.getIndex());
    }

    public void readFromNBT(NBTTagCompound compound, IToggleController controller)
    {
        int x = compound.getInteger("X");
        int y = compound.getInteger("Y");
        int z = compound.getInteger("Z");
        this.pos = new BlockPos(x, y, z);

        NBTTagList overrideList = compound.getTagList("OverrideList", 10);
        this.overrides = new StateOverride[controller.getStateCount()];
        for (int i = 0; i < overrideList.tagCount(); i++)
        {
            NBTTagCompound stateCompound = overrideList.getCompoundTagAt(i);
            if (stateCompound == null) continue;
            int state = stateCompound.getInteger("State");
            StateOverride override = new StateOverride(stateCompound);
            this.overrides[state] = override;
        }

        this.direction = EnumFacing.getFront(compound.getInteger("Direction"));
    }

    public void removeIfChangeBlock(World world, BlockPos pos, IToggleController controller)
    {
        if (world.getBlockState(pos).getBlock() == BlockBase.change_block)
            world.setBlockToAir(pos);
    }

    public void placeChangeBlock(World world, BlockPos pos, EntityPlayer player, IToggleController controller)
    {
        ItemStack[] items = harvest(world, player, controller);
        if (items != null) controller.getStorageHandler().addItemStacksToStorage(items);
        world.setBlockState(pos, BlockBase.change_block.getStateFromMeta(direction.getIndex()));
    }

    public ItemStack getOverrideStack(int state)
    {
        return overrides[state] != null && overrides[state].overrides ? overrides[state].stack : null;
    }

    public void setOverrideStack(int state, ItemStack stack)
    {
        if (overrides[state] == null) overrides[state] = new StateOverride(stack, false);
        else overrides[state].setStack(stack);
    }

    private ItemStack copy(ItemStack stack)
    {
        return stack == null ? null : stack.copy();
    }

    public void doActionForState(World world, int state, EntityPlayer player, ItemStack stack, IToggleController controller)
    {
        ItemStack[] harvested = this.harvest(world, player, controller);
        if (harvested != null) controller.getStorageHandler().addItemStacksToStorage(harvested);
        ItemStack placing = controller.getStorageHandler().getItemFromStorage(stack);
        this.place(world, player, placing, controller);
    }

    private void place(World world, EntityPlayer player, ItemStack placing, IToggleController controller)
    {
        List<IBlockToggleAction> actions = ToggleActionRegistry.instance().getRegisteredActions();
        boolean placed = false;
        if (placing != null)
            for (IBlockToggleAction action : actions)
                if (action.canPlaceBlock(world, pos, placing, controller))
                {
                    action.placeBlock(world, pos, getDirection(), player, placing, controller);
                    placed = true;
                    break;
                }
        if (!placed)
            FALLBACK_ACTION.placeBlock(world, pos, getDirection(), player, placing, controller);
    }

    private ItemStack[] harvest(World world, EntityPlayer player, IToggleController controller)
    {
        List<IBlockToggleAction> actions = ToggleActionRegistry.instance().getRegisteredActions();
        for (IBlockToggleAction action : actions)
            if (action.canHarvestBlock(world, pos, controller))
                return action.harvestBlock(world, pos, player, controller);
        return FALLBACK_ACTION.harvestBlock(world, pos, player, controller);
    }

    public EnumFacing getDirection()
    {
        return direction;
    }

    public boolean overridesState(int state)
    {
        return overrides[state] != null && overrides[state].overrides;
    }

    public BlockPos getPos()
    {
        return pos;
    }

    class StateOverride
    {
        ItemStack stack;
        boolean overrides;

        StateOverride(ItemStack stack, boolean overrides)
        {
            this.setStack(stack);
            this.overrides = overrides;
        }

        StateOverride(NBTTagCompound compound)
        {
            this.readFromNBT(compound);
        }

        public void writeToNBT(NBTTagCompound compound)
        {
            compound.setBoolean("Overrides", this.overrides);
            NBTTagCompound itemCompound = new NBTTagCompound();
            if (stack != null) this.stack.writeToNBT(itemCompound);
            compound.setTag("OverridesWith", itemCompound);
        }

        public void readFromNBT(NBTTagCompound compound)
        {
            if (compound.hasKey("Overrides"))
                this.overrides = compound.getBoolean("Overrides");
            if (compound.hasKey("OverridesWith", 10))
                this.stack = ItemStack.loadItemStackFromNBT(compound.getCompoundTag("OverridesWith"));
        }

        public void setStack(ItemStack stack)
        {
            this.stack = copy(stack);
            this.stack.stackSize = 1;
        }
    }
}
