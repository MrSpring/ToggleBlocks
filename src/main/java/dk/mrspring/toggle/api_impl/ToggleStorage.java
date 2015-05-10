package dk.mrspring.toggle.api_impl;

import dk.mrspring.toggle.api.IToggleStorage;
import dk.mrspring.toggle.api.StoragePriority;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

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
        return null;
    }

    @Override
    public ItemStack getToolFromStorage(String toolType)
    {
        return null;
    }
}
