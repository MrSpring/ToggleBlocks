package dk.mrspring.toggle.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

/**
 * Created by Konrad on 27-02-2015.
 */
public class TileEntityChangeBlock extends TileEntity implements IInventory
{
    boolean[] overrideState = new boolean[]{false, false};
    ItemStack[] overrideStates = new ItemStack[overrideState.length];

    public TileEntityChangeBlock()
    {
        super();
    }

    public TileEntityChangeBlock(int x, int y, int z, ChangeBlockInfo info)
    {
        super();

        xCoord = x;
        yCoord = y;
        zCoord = z;

        this.loadFromBlockInfo(info);
    }

    public ChangeBlockInfo getBlockInfo()
    {
        ChangeBlockInfo info = new ChangeBlockInfo(xCoord, yCoord, zCoord);
        info.setOverride(overrideState);
        info.setOverrideStates(overrideStates);
        return info;
    }

    public void loadFromBlockInfo(ChangeBlockInfo info)
    {
        this.overrideState = info.getOverrides();
        System.out.println(overrideState[0]);
        System.out.println(overrideState[1]);
        this.overrideStates = info.getOverrideStates();
        if (overrideStates[0] != null)
            System.out.println(overrideStates[0].getDisplayName());
        else System.out.println("0 is null");
        if (overrideStates[1] != null)
            System.out.println(overrideStates[1].getDisplayName());
        else System.out.println("1 is null");
    }

    public void setOverride(int state, boolean override)
    {
        if (state >= 0 && state < overrideState.length)
        {
            System.out.println("Setting state: " + state + " to: " + override);
            overrideState[state] = override;
            if (!override)
                overrideStates[state] = null;
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }
    }

    public boolean overrides(int state)
    {
        if (state >= 0 && state < overrideState.length)
            return overrideState[state];
        else return false;
    }

    public boolean[] getOverridesStates()
    {
        return overrideState;
    }

    @Override
    public void writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);

        NBTTagList overrideList = new NBTTagList();
        for (int i = 0; i < overrideState.length; i++)
        {
            NBTTagCompound overrideEntry = new NBTTagCompound();
            boolean overrides = overrides(i);
            ItemStack overrideStack = overrideStates[i];
            overrideEntry.setInteger("State", i);
            overrideEntry.setBoolean("Overrides", overrides);
            if (overrides)
                if (overrideStack != null)
                    overrideStack.writeToNBT(overrideEntry);
            overrideList.appendTag(overrideEntry);
        }

        compound.setTag("OverrideList", overrideList);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);

        NBTTagList overrideList = compound.getTagList("OverrideList", 10);
        overrideState = new boolean[overrideList.tagCount()];
        overrideStates = new ItemStack[overrideState.length];
        for (int i = 0; i < overrideList.tagCount(); i++)
        {
            NBTTagCompound overrideEntry = overrideList.getCompoundTagAt(i);
            int state = overrideEntry.getInteger("State");
            boolean overrides = overrideEntry.getBoolean("Overrides");
            overrideState[state] = overrides;
            if (overrides)
            {
                ItemStack overrideStack = ItemStack.loadItemStackFromNBT(overrideEntry);
                overrideStates[i] = overrideStack;
            }
        }
    }

    @Override
    public Packet getDescriptionPacket()
    {
        NBTTagCompound compound = new NBTTagCompound();
        this.writeToNBT(compound);
        return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 2, compound);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
    {
        this.readFromNBT(pkt.func_148857_g());
    }

    @Override
    public int getSizeInventory()
    {
        return overrideState.length;
    }

    @Override
    public ItemStack getStackInSlot(int slot)
    {
        return overrideStates[slot];
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount)
    {
        overrideStates[slot] = null;
        return null;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot)
    {
        return null;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack)
    {
        if (stack != null && overrideState[slot])
        {
            this.overrideStates[slot] = stack.copy();
            this.overrideStates[slot].stackSize = 1;
        }
    }

    @Override
    public String getInventoryName()
    {
        return "Change Block";
    }

    @Override
    public boolean hasCustomInventoryName()
    {
        return false;
    }

    @Override
    public int getInventoryStackLimit()
    {
        return 0;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer p_70300_1_)
    {
        return true;
    }

    @Override
    public void openInventory()
    {

    }

    @Override
    public void closeInventory()
    {

    }

    @Override
    public boolean isItemValidForSlot(int p_94041_1_, ItemStack p_94041_2_)
    {
        return true;
    }
}
