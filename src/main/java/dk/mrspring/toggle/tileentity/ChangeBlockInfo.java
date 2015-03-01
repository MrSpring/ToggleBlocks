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

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 * Created by Konrad on 27-02-2015.
 */
public class ChangeBlockInfo
{
    public int x, y, z;
    BlockToggleAction on, off;
    boolean[] override;
    ItemStack[] overrideStates;

    public ChangeBlockInfo(int x, int y, int z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.setOnAction(new BlockToggleAction());
        this.setOffAction(new BlockToggleAction());
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
        this.setOnAction(new BlockToggleAction());
        this.setOffAction(new BlockToggleAction());
        this.readFromNBT(compound);
    }

    public ChangeBlockInfo setOnAction(BlockToggleAction on)
    {
        this.on = on;
        return this;
    }

    public ChangeBlockInfo setOffAction(BlockToggleAction off)
    {
        this.off = off;
        return this;
    }

    public BlockToggleAction getAction(int state)
    {
        if (state == 1)
            return on;
        else return off;
    }

    public void writeToNBT(NBTTagCompound compound)
    {
        compound.setInteger("X", x);
        compound.setInteger("Z", y);
        compound.setInteger("Y", z);
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

    public static class BlockToggleAction implements IBlockToggleAction
    {
        /**
         * @param world      World object
         * @param x          The X coordinate of the block to change
         * @param y          The Y coordinate of the block to change
         * @param z          The Z coordinate of the block to change
         * @param direction  The direction the change block is configured with
         * @param player     The player
         * @param placing    The ItemStack to place, remember to reduce stack size! When null the block
         *                   should simply be left as air
         * @param tileEntity The TileEntity of the toggle block
         */
        public boolean performAction(World world, int x, int y, int z, int direction, EntityPlayer player,
                                     ItemStack placing, IToggleController tileEntity)
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
            tileEntity.addItemStacksToStorage(items);
            world.setBlockToAir(x, y, z);
            if (placing != null)
            {
                player.setItemInUse(placing, 0);
                if (placing.tryPlaceItemIntoWorld(player, world, x, y, z, 0, 0, 0, 0))
                {
//                    placing.stackSize--;
                    // TODO: Use how if seeds etc.
                } else
                {
                    List<IBlockToggleAction> actions = ToggleRegistry.instance.getRegisteredActions();
                    for (IBlockToggleAction action : actions)
                    {
                        if (action.useWithItem(world, x, y, z, placing, tileEntity))
                            if (action.performAction(world, x, y, z, direction, player, placing, tileEntity)) break;
                    }
                }
            }

            return true;
        }

        @Override
        public boolean useWithItem(World world, int x, int y, int z, ItemStack placing, IToggleController tileEntity)
        {
            return true;
        }
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
