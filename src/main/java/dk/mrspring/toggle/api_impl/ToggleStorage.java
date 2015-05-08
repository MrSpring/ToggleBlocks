package dk.mrspring.toggle.api_impl;

import dk.mrspring.toggle.api.IToggleStorage;
import dk.mrspring.toggle.api.StoragePriority;
import net.minecraft.item.ItemStack;

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
            while (toAdd.stackSize > 0)
                for (int i = 0; i < itemStacks.length; i++)
                {
                    ItemStack inSlot = itemStacks[i];
                    if (inSlot == null)
                    {
                        itemStacks[i] = toAdd;
                        break;
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
                            break;
                        }
                    }
                }
        }
    }

    @Override
    public StoragePriority getStoragePriority()
    {
        return null;
    }

    @Override
    public ItemStack removeStackFromStorage(ItemStack stack)
    {

    }

    @Override
    public ItemStack[] removeAllStacksFromStorage(ItemStack stack)
    {

    }

    @Override
    public ItemStack requestItemFromStorage(ItemStack item)
    {
        return null;
    }

    @Override
    public ItemStack requestToolFromStorage(String toolType)
    {
        return null;
    }
}
