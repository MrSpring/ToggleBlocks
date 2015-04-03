package dk.mrspring.toggle.tileentity;

import com.mojang.authlib.GameProfile;
import dk.mrspring.toggle.ToggleRegistry;
import dk.mrspring.toggle.api.IBlockToggleAction;
import dk.mrspring.toggle.api.IToggleController;
import dk.mrspring.toggle.block.BlockBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.List;
import java.util.UUID;

/**
 * Created by Konrad on 27-02-2015.
 */
public class ChangeBlockInfo
{
    public static final IBlockToggleAction FALLBACK_ACTION = new BasicBlockToggleAction();

    public int x, y, z;
    //    boolean[] override;
//    ItemStack[] overrideStates;
    StateOverride[] overrides = new StateOverride[2];
    ForgeDirection direction = ForgeDirection.DOWN;

    public ChangeBlockInfo(int x, int y, int z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
//        this.override = new boolean[]{false, false};
//        this.overrideStates = new ItemStack[override.length];
        this.overrides = new StateOverride[]{
                new StateOverride(false),
                new StateOverride(false)
        };
    }

//    public void setOverride(boolean[] override)
//    {
//        this.override = override;
//    }
//
//    public void setOverrideStates(ItemStack[] overrideStates)
//    {
//        this.overrideStates = overrideStates;
//    }

    public ChangeBlockInfo(NBTTagCompound compound)
    {
        this.readFromNBT(compound, true);
    }

    public ChangeBlockInfo updateCoordinates(int newX, int newY, int newZ)
    {
        this.x = newX;
        this.y = newY;
        this.z = newZ;
        return this;
    }

    public void doAction(World world, int state, EntityPlayer player, ItemStack defaultPlacing,
                         IToggleController controller)
    {
        ItemStack[] harvested = this.harvest(world, player, controller);
        if (harvested != null)
            controller.addItemStacksToStorage(harvested);

        ItemStack placing = controller.requestItemFromStorage(defaultPlacing);
        if (overridesState(state))
        {
            ItemStack overrider = getOverrideStackForState(state);
            placing = controller.requestItemFromStorage(overrider);
        }

        this.place(world, player, placing, controller);
    }

    private void place(World world, EntityPlayer player, ItemStack placing, IToggleController controller)
    {
        List<IBlockToggleAction> actions = ToggleRegistry.instance.getRegisteredActions();
        boolean placed = false;
        if (placing != null)
            for (IBlockToggleAction action : actions)
                if (action.canPlaceBlock(world, x, y, z, placing, controller))
                {
                    action.placeBlock(world, x, y, z, getDirection(), player, placing, controller);
                    placed = true;
                    break;
                }
        if (!placed)
            FALLBACK_ACTION.placeBlock(world, x, y, z, getDirection(), player, placing, controller);
    }

    private ItemStack[] harvest(World world, EntityPlayer player, IToggleController controller)
    {
        List<IBlockToggleAction> actions = ToggleRegistry.instance.getRegisteredActions();
        for (IBlockToggleAction action : actions)
        {
            if (action.canHarvestBlock(world, x, y, z, controller))
            {
                return action.harvestBlock(world, x, y, z, player, controller);
            }
        }
        return FALLBACK_ACTION.harvestBlock(world, x, y, z, player, controller);
    }

    public void replaceWithChangeBlock(World world, EntityPlayer player, IToggleController controller)
    {
        controller.addItemStacksToStorage(this.harvest(world, player, controller));
        world.setBlock(x, y, z, BlockBase.change_block);
        world.setTileEntity(x, y, z, new TileEntityChangeBlock(x, y, z, this));
    }

    /*public BlockToggleAction getAction(int state)
    {
        if (state == 1)
            return on;
        else return off;
    }*/

    public void writeToNBT(NBTTagCompound compound, boolean writeCoords)
    {
        if (writeCoords)
        {
            compound.setInteger("X", x);
            compound.setInteger("Y", y);
            compound.setInteger("Z", z);
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
            this.x = compound.getInteger("X");
            this.y = compound.getInteger("Y");
            this.z = compound.getInteger("Z");
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
        this.direction = ForgeDirection.getOrientation(direction);
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

    public ForgeDirection getDirection()
    {
        return direction;
    }

    public static class FakePlayer extends EntityPlayer
    {
        public FakePlayer(World world)
        {
            super(world, new GameProfile(new UUID(0, 0), "ToggleBlock"));
        }

        @Override
        public void addChatMessage(IChatComponent p_145747_1_)
        {

        }

        @Override
        public boolean canCommandSenderUseCommand(int p_70003_1_, String p_70003_2_)
        {
            return false;
        }

        @Override
        public ChunkCoordinates getPlayerCoordinates()
        {
            return null;
        }
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
