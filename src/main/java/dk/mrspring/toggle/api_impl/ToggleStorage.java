package dk.mrspring.toggle.api_impl;

import dk.mrspring.toggle.api.IToggleStorage;
import dk.mrspring.toggle.api.StoragePriority;
import dk.mrspring.toggle.tileentity.TileEntityToggleBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Konrad on 07-05-2015.
 */
public class ToggleStorage implements IToggleStorage
{
    ItemStack[] itemStacks;

    public ToggleStorage(int amount)
    {
        itemStacks = new ItemStack[amount];
    }

    public ToggleStorage(NBTTagCompound compound, int amount)
    {
        this.readFromNBT(compound);
        if (this.itemStacks == null)
            this.itemStacks = new ItemStack[amount];
    }

    @Override
    public void validateStorage()
    {
        for (int i = 0; i < itemStacks.length; i++)
        {
            ItemStack stack = itemStacks[i];
            if (stack != null)
                if (stack.stackSize == 0)
                    itemStacks[i] = null;
        }
    }

    @Override
    public ItemStack[] addItemStacksToStorage(ItemStack[] stacks)
    {
        ItemStack[] returning = new ItemStack[stacks.length];
        for (int i = 0; i < stacks.length; i++)
        {
            ItemStack stack = stacks[i];
            if (stack != null)
                if (stack.stackSize > 0)
                    returning[i] = this.addItemStackToStorage(stack);
        }
        return returning;
    }

    @Override
    public ItemStack addItemStackToStorage(ItemStack stack)
    {
        if (stack != null)
        {
            ItemStack toAdd = stack.copy();
            for (int i = 0; i < itemStacks.length && toAdd.stackSize > 0; i++)
            {
                ItemStack inSlot = itemStacks[i];
                if (inSlot == null)
                {
                    itemStacks[i] = toAdd;
                    toAdd.stackSize = 0;
                } else
                {
                    if (inSlot.isItemEqual(toAdd) && ItemStack.areItemStackTagsEqual(inSlot, toAdd))
                    {
                        inSlot.stackSize += toAdd.stackSize;
                        int maxStackSize = inSlot.getMaxStackSize();
                        if (inSlot.stackSize > maxStackSize)
                        {
                            toAdd.stackSize = inSlot.stackSize - maxStackSize;
                            inSlot.stackSize = maxStackSize;
                        } else toAdd.stackSize = 0;
                    }
                }
            }
            if (toAdd.stackSize > 0)
                return toAdd;
        }
        return null;
    }

    @Override
    public StoragePriority getStoragePriority()
    {
        return StoragePriority.STORAGE_FIRST;
    }

    @Override
    public ItemStack removeStackFromStorage(ItemStack stack)
    {
        for (int i = 0; i < itemStacks.length; i++)
        {
            ItemStack inSlot = itemStacks[i];
            if (inSlot != null)
                if (inSlot.isItemEqual(stack) && ItemStack.areItemStackTagsEqual(inSlot, stack))
                {
                    ItemStack returning = inSlot.copy();
                    itemStacks[i] = null;
                    return returning;
                }
        }
        return null;
    }

    @Override
    public ItemStack[] removeAllStacksFromStorage(ItemStack stack)
    {
        List<ItemStack> list = new ArrayList<ItemStack>();
        for (int i = 0; i < itemStacks.length; i++)
        {
            ItemStack inSlot = itemStacks[i];
            if (inSlot != null)
                if (inSlot.isItemEqual(stack) && ItemStack.areItemStackTagsEqual(inSlot, stack))
                {
                    ItemStack returning = inSlot.copy();
                    itemStacks[i] = null;
                    list.add(returning);
                }
        }
        return list.toArray(new ItemStack[list.size()]);
    }

    @Override
    public ItemStack getItemFromStorage(ItemStack item)
    {
        for (ItemStack storageStack : this.itemStacks)
        {
            if (storageStack != null && item != null)
                if (storageStack.isItemEqual(item))
                    return storageStack;
        }
        return null;
    }

    @Override
    public ItemStack getToolFromStorage(String toolType)
    {
        for (ItemStack stack : this.itemStacks)
            if (stack != null)
                if (stack.getItem().getToolClasses(stack).contains(toolType))
                    return stack;
                else if (TileEntityToggleBlock.toolTypeClasses.containsKey(toolType) && stack.getItem().getClass() == TileEntityToggleBlock.toolTypeClasses.get(toolType))
                {
                    System.out.println("Returning type: " + toolType);
                    return stack;
                }
        return null;
    }

    @Override
    public int getStorageSlots()
    {
        return 9;
    }

    @Override
    public ItemStack getItemFromSlot(int slot)
    {
        return (slot >= 0 && slot < itemStacks.length) ? itemStacks[slot] : null;
    }

    @Override
    public void setItemInSlot(int slot, ItemStack stack)
    {
        if (slot >= 0 && slot < itemStacks.length)
            this.itemStacks[slot] = stack;
    }

    @Override
    public void writeToNBT(NBTTagCompound compound)
    {
        NBTTagList storageList = new NBTTagList();

        for (int i = 0; i < this.itemStacks.length; ++i)
        {
            if (this.itemStacks[i] != null)
            {
                NBTTagCompound storageCompound = new NBTTagCompound();
                storageCompound.setByte("Slot", (byte) i);
                this.itemStacks[i].writeToNBT(storageCompound);
                storageList.appendTag(storageCompound);
            }
        }

        compound.setTag("Items", storageList);
        compound.setString("StoragePriority", getStoragePriority().name());
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        NBTTagList storageList = compound.getTagList("Items", 10);
        this.itemStacks = new ItemStack[getStorageSlots()];

        for (int i = 0; i < storageList.tagCount(); i++)
        {
            NBTTagCompound itemCompound = storageList.getCompoundTagAt(i);
            ItemStack fromCompound = ItemStack.loadItemStackFromNBT(itemCompound);
            this.itemStacks[itemCompound.getByte("Slot")] = fromCompound;
        }
    }
}
