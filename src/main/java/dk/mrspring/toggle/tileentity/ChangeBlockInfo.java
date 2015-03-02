package dk.mrspring.toggle.tileentity;

import com.mojang.authlib.GameProfile;
import dk.mrspring.toggle.ToggleRegistry;
import dk.mrspring.toggle.api.IBlockToggleAction;
import dk.mrspring.toggle.api.IToggleController;
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
    boolean[] override;
    ItemStack[] overrideStates;
    ForgeDirection direction = ForgeDirection.DOWN;

    public ChangeBlockInfo(int x, int y, int z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.override = new boolean[]{false, false};
        this.overrideStates = new ItemStack[override.length];
    }

    public void setOverride(boolean[] override)
    {
        this.override = override;
    }

    public void setOverrideStates(ItemStack[] overrideStates)
    {
        this.overrideStates = overrideStates;
    }

    public ChangeBlockInfo(NBTTagCompound compound)
    {
        this.readFromNBT(compound);
    }

    public void doAction(World world, int state, EntityPlayer player, ItemStack defaultPlacing,
                         IToggleController controller)
    {
        boolean harvested = false;
        List<IBlockToggleAction> actions = ToggleRegistry.instance.getRegisteredActions();
        for (IBlockToggleAction action : actions)
        {
            if (action.canHarvestBlock(world, x, y, z, controller))
            {
                ItemStack[] fromBlock = action.harvestBlock(world, x, y, z, player, controller);
                controller.addItemStacksToStorage(fromBlock);
                harvested = true;
                break;
            }
        }
        if (!harvested)
        {
            ItemStack[] fromBlock = FALLBACK_ACTION.harvestBlock(world, x, y, z, player, controller);
            controller.addItemStacksToStorage(fromBlock);
        }

        ItemStack placing = controller.requestItemFromStorage(defaultPlacing);
        if (overridesState(state))
        {
            ItemStack overrider = getOverrideForState(state);
            placing = controller.requestItemFromStorage(overrider);
        }
        boolean placed = false;
        if (placing != null)
            for (IBlockToggleAction action : actions)
                if (action.canPerformAction(world, x, y, z, placing, controller))
                {
                    action.performAction(world, x, y, z, getDirection(), player, placing, controller);
                    placed = true;
                    break;
                }
        if (!placed)
            FALLBACK_ACTION.performAction(world, x, y, z, getDirection(), player, placing, controller);
    }

    public void replaceWithChangeBlock(World world, IToggleController controller)
    {

    }

    /*public BlockToggleAction getAction(int state)
    {
        if (state == 1)
            return on;
        else return off;
    }*/

    public void writeToNBT(NBTTagCompound compound)
    {
        compound.setInteger("X", x);
        compound.setInteger("Y", y);
        compound.setInteger("Z", z);
        NBTTagList overrideList = new NBTTagList();
        for (int i = 0; i < override.length; i++)
        {
            boolean overrides = override[i];
            ItemStack overridesWith = getOverrideForState(i);
            NBTTagCompound stateCompound = new NBTTagCompound();
            stateCompound.setInteger("State", i);
            stateCompound.setBoolean("Overrides", overrides);
            if (overrides && overridesWith != null)
                overridesWith.writeToNBT(stateCompound);
            overrideList.appendTag(stateCompound);
        }
        compound.setTag("OverrideList", overrideList);
        compound.setInteger("Direction", direction.ordinal());
    }

    public void readFromNBT(NBTTagCompound compound)
    {
        this.x = compound.getInteger("X");
        this.y = compound.getInteger("Y");
        this.z = compound.getInteger("Z");
        NBTTagList overrideList = compound.getTagList("OverrideList", 10);
        if (overrideList.tagCount() > 0)
        {
            this.override = new boolean[overrideList.tagCount()];
            this.overrideStates = new ItemStack[this.override.length];
            for (int i = 0; i < overrideList.tagCount(); i++)
            {
                NBTTagCompound stateCompound = overrideList.getCompoundTagAt(i);
                if (stateCompound != null)
                {
                    int state = stateCompound.getInteger("State");
                    boolean overrides = stateCompound.getBoolean("Overrides");
                    ItemStack stack = ItemStack.loadItemStackFromNBT(stateCompound);

                    this.override[state] = overrides;
                    overrideStates[state] = stack;
                }
            }
        }
        direction = ForgeDirection.getOrientation(compound.getInteger("Direction"));
    }

    public boolean overridesState(int state)
    {
        if (state >= 0 && state < this.override.length)
            return this.override[state];
        else return false;
    }

    public ItemStack getOverrideForState(int state)
    {
        return this.overrideStates[state];
    }

    public boolean[] getOverrides()
    {
        return override;
    }

    public ItemStack[] getOverrideStates()
    {
        return overrideStates;
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


}
