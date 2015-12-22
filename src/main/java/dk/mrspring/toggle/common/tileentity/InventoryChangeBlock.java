package dk.mrspring.toggle.common.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IChatComponent;

/**
 * Created on 21-12-2015 for ToggleBlocks.
 */
public class InventoryChangeBlock implements IInventory
{
    ChangeBlock changeBlock;

    public InventoryChangeBlock(ChangeBlock block)
    {
        this.changeBlock = block;
    }

    @Override
    public int getSizeInventory()
    {
        return changeBlock.overrides.length;
    }

    @Override
    public ItemStack getStackInSlot(int index)
    {
        return changeBlock.getOverrideStack(index);
    }

    @Override
    public ItemStack decrStackSize(int index, int count)
    {
        changeBlock.setOverrideStack(index, null);
        return null;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int index)
    {
        return null;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack)
    {
        changeBlock.setOverrideStack(index, stack);
    }

    @Override
    public int getInventoryStackLimit()
    {
        return 0;
    }

    @Override
    public void markDirty()
    {

    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player)
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
    public boolean isItemValidForSlot(int index, ItemStack stack)
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

    @Override
    public String getCommandSenderName()
    {
        return null;
    }

    @Override
    public boolean hasCustomName()
    {
        return false;
    }

    @Override
    public IChatComponent getDisplayName()
    {
        return null;
    }

    public ChangeBlock getChangeBlock()
    {
        return changeBlock;
    }
}
