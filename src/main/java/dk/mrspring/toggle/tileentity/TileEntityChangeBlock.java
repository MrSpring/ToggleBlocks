package dk.mrspring.toggle.tileentity;

import dk.mrspring.toggle.block.BlockBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.StatCollector;

/**
 * Created by Konrad on 27-02-2015.
 */
public class TileEntityChangeBlock extends TileEntity implements IInventory
{
    ChangeBlockInfo info;
    BlockPos cpos;

    public TileEntityChangeBlock()
    {
        super();
    }

    public TileEntityChangeBlock(BlockPos pos, ChangeBlockInfo info)
    {
        super();

        this.setControllerPos(pos);

        this.setBlockInfo(info);
    }

    public ChangeBlockInfo getBlockInfo()
    {
        if (info == null)
        {
            IBlockState state = worldObj.getBlockState(getPos());
            info = new ChangeBlockInfo(getPos(), BlockBase.change_block.getDirectionFromState(state));
        }
        info.setCoordinates(getPos());
        return info;
    }

    public void setBlockInfo(ChangeBlockInfo newInfo)
    {
        this.info = newInfo;
    }

    @Override
    public void writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        this.getBlockInfo().writeToNBT(compound, false);
        compound.setInteger("ControllerX", cpos.getX());
        compound.setInteger("ControllerY", cpos.getY());
        compound.setInteger("ControllerZ", cpos.getZ());
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        this.getBlockInfo().readFromNBT(compound, false);
        int cx = compound.getInteger("ControllerX");
        int cy = compound.getInteger("ControllerY");
        int cz = compound.getInteger("ControllerZ");
        this.cpos = new BlockPos(cx, cy, cz);
    }

    @Override
    public Packet getDescriptionPacket()
    {
        NBTTagCompound compound = new NBTTagCompound();
        this.writeToNBT(compound);
        return new S35PacketUpdateTileEntity(getPos(), 2, compound);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
    {
        this.readFromNBT(pkt.getNbtCompound());
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
    public String getName()
    {
        return StatCollector.translateToLocal("tile.change_block.container.name");
    }

    @Override
    public boolean hasCustomName()
    {
        return false;
    }

    @Override
    public IChatComponent getDisplayName()
    {
        return new ChatComponentText(getName());
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
    public void openInventory(EntityPlayer player)
    {
    }

    @Override
    public void closeInventory(EntityPlayer player)
    {
    }

    @Override
    public boolean isItemValidForSlot(int p_94041_1_, ItemStack p_94041_2_)
    {
        return true;
    }

    @Override
    public int getField(int id)
    {
        return 0;
    }

    @Override
    public void setField(int id, int value)
    {

    }

    @Override
    public int getFieldCount()
    {
        return 0;
    }

    @Override
    public void clear()
    {

    }

    public void setControllerPos(BlockPos pos)
    {
        this.cpos = pos;
    }

    public BlockPos getCPos()
    {
        return cpos;
    }
}
