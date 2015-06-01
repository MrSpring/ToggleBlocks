package dk.mrspring.toggle.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;

/**
 * Created by Konrad on 27-02-2015.
 */
public class TileEntityChangeBlock extends TileEntity implements IInventory // TODO: Get mostly rid of this class, use only for opening gui. Get the overrides from the Toggle Controller
{
    ChangeBlockInfo info;

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

        this.setBlockInfo(info);
    }

    public ChangeBlockInfo getBlockInfo()
    {
        if (info == null)
            info = new ChangeBlockInfo(xCoord, yCoord, zCoord, blockMetadata);
        info.setCoordinates(xCoord, yCoord, zCoord);
        return info;
    }

    public void setBlockInfo(ChangeBlockInfo newInfo)
    {
        this.info = newInfo;
    }

    @Override
    public void updateEntity()
    {
        if (info != null && getBlockMetadata() != info.getDirectionID())
        {
            worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, info.getDirectionID(), 2);
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        this.getBlockInfo().writeToNBT(compound, false);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        this.getBlockInfo().readFromNBT(compound, false);
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
        return getBlockInfo().overrides.length;
    }

    @Override
    public ItemStack getStackInSlot(int slot)
    {
        return getBlockInfo().getOverrideStackForState(slot);
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount)
    {
        getBlockInfo().setOverrideStackForState(slot, null);
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
        if (stack != null && getBlockInfo().overridesState(slot))
        {
            ItemStack copy = stack.copy();
            copy.stackSize = 1;
            getBlockInfo().setOverrideStackForState(slot, copy);
        }
    }

    @Override
    public String getInventoryName()
    {
        return StatCollector.translateToLocal("tile.change_block.container.name");
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
