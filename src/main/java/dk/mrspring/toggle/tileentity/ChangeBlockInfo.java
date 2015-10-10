package dk.mrspring.toggle.tileentity;

import dk.mrspring.toggle.ToggleRegistry;
import dk.mrspring.toggle.api.IBlockToggleAction;
import dk.mrspring.toggle.api.IChangeBlockInfo;
import dk.mrspring.toggle.api.IToggleController;
import dk.mrspring.toggle.block.BlockBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import java.util.List;

/**
 * Created by Konrad on 27-02-2015.
 */
public class ChangeBlockInfo implements IChangeBlockInfo
{
    public static final IBlockToggleAction FALLBACK_ACTION = new BasicBlockToggleAction();

    public BlockPos pos;
    StateOverride[] overrides = new StateOverride[2];
    EnumFacing direction = EnumFacing.DOWN;

    public ChangeBlockInfo(BlockPos pos, EnumFacing direction)
    {
        this.pos = pos;
        this.overrides = new StateOverride[]{
                new StateOverride(false),
                new StateOverride(false)
        };
        this.direction = direction;
    }

    public ChangeBlockInfo(NBTTagCompound compound)
    {
        this.readFromNBT(compound, true);
    }

    public void setCoordinates(BlockPos newPos)
    {
        this.pos = newPos;
    }

    public void doActionForState(World world, int state, EntityPlayer player, ItemStack defaultPlacing,
                                 IToggleController controller)
    {
        ItemStack[] harvested = this.harvest(world, player, controller);
        if (harvested != null)
            controller.getStorageHandler().addItemStacksToStorage(harvested);

        ItemStack placing = controller.getStorageHandler().getItemFromStorage(defaultPlacing);
        if (overridesState(state))
        {
            ItemStack overrider = getOverrideStackForState(state);
            placing = controller.getStorageHandler().getItemFromStorage(overrider);
        }

        this.place(world, player, placing, controller);
    }

    private void place(World world, EntityPlayer player, ItemStack placing, IToggleController controller)
    {
        List<IBlockToggleAction> actions = ToggleRegistry.instance().getRegisteredActions();
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
        List<IBlockToggleAction> actions = ToggleRegistry.instance().getRegisteredActions();
        for (IBlockToggleAction action : actions)
        {
            if (action.canHarvestBlock(world, pos, controller))
            {
                return action.harvestBlock(world, pos, player, controller);
            }
        }
        return FALLBACK_ACTION.harvestBlock(world, pos, player, controller);
    }

    public void placeChangeBlock(World world, EntityPlayer player, IToggleController controller)
    {
        controller.getStorageHandler().addItemStacksToStorage(this.harvest(world, player, controller));
//        world.setBlock(x, y, z, BlockBase.change_block);
//        world.setBlockMetadataWithNotify(x, y, z, getDirectionID(), 2);
        world.setBlockState(pos, BlockBase.change_block.makeStateFromDirection(getDirection()));
        world.setTileEntity(pos, new TileEntityChangeBlock(pos, this));
        ((TileEntityChangeBlock) world.getTileEntity(pos)).setControllerPos(controller.pos());
    }

    public void writeToNBT(NBTTagCompound compound, boolean writeCoords)
    {
        if (writeCoords)
        {
            compound.setInteger("X", pos.getX());
            compound.setInteger("Y", pos.getY());
            compound.setInteger("Z", pos.getZ());
        }

        NBTTagList overrideList = new NBTTagList();
        for (int i = 0; i < overrides.length; i++)
        {
            StateOverride override = overrides[i];
            NBTTagCompound stateCompound = new NBTTagCompound();

            override.writeToNBT(stateCompound);
            stateCompound.setInteger("State", i);
            overrideList.appendTag(stateCompound);
        }
        compound.setTag("OverrideList", overrideList);
        compound.setInteger("Direction", direction.ordinal());
    }

    public void readFromNBT(NBTTagCompound compound, boolean readCoords)
    {
        if (readCoords)
        {
            int x = compound.getInteger("X");
            int y = compound.getInteger("Y");
            int z = compound.getInteger("Z");
            this.pos = new BlockPos(x, y, z);
        }

        NBTTagList overrideList = compound.getTagList("OverrideList", 10);
        if (overrideList.tagCount() > 0)
        {
            this.overrides = new StateOverride[overrideList.tagCount()];
            for (int i = 0; i < overrideList.tagCount(); i++)
            {
                NBTTagCompound stateCompound = overrideList.getCompoundTagAt(i);
                if (stateCompound != null)
                {
                    int state = stateCompound.getInteger("State");
                    StateOverride overrides = new StateOverride(stateCompound);
                    this.overrides[state] = overrides;
                }
            }
        }
        int direction = compound.getInteger("Direction");
        this.direction = EnumFacing.getFront(direction);
    }

    private StateOverride getOverrideForState(int state)
    {
        if (state >= 0 && state < this.overrides.length)
            return overrides[state];
        else return new StateOverride();
    }

    public boolean overridesState(int state)
    {
        return getOverrideForState(state).overrides;
    }

    public void setOverridesState(int state, boolean doesOverride)
    {
        getOverrideForState(state).overrides = doesOverride;
    }

    public ItemStack getOverrideStackForState(int state)
    {
        return getOverrideForState(state).overridesWith;
    }

    public void setOverrideStackForState(int state, ItemStack overrider)
    {
        getOverrideForState(state).overridesWith = overrider;
    }

    public EnumFacing getDirection()
    {
        return direction;
    }

    public void setDirection(EnumFacing direction)
    {
        this.direction = direction;
    }

    public static class StateOverride
    {
        public boolean overrides = false;
        public ItemStack overridesWith = null;

        public StateOverride(boolean does, ItemStack with)
        {
            this.overrides = does;
            this.overridesWith = with;
        }

        public StateOverride(boolean does)
        {
            this.overrides = does;
        }

        public StateOverride()
        {
        }

        public StateOverride(NBTTagCompound compound)
        {
            this.readFromNBT(compound);
        }

        public void writeToNBT(NBTTagCompound compound)
        {
            compound.setBoolean("Overrides", this.overrides);
            NBTTagCompound itemCompound = new NBTTagCompound();
            if (overridesWith != null)
                this.overridesWith.writeToNBT(itemCompound);
            compound.setTag("OverridesWith", itemCompound);
        }

        public void readFromNBT(NBTTagCompound compound)
        {
            if (compound.hasKey("Overrides"))
                this.overrides = compound.getBoolean("Overrides");
            if (compound.hasKey("OverridesWith", 10))
                this.overridesWith = ItemStack.loadItemStackFromNBT(compound.getCompoundTag("OverridesWith"));
        }
    }
}
