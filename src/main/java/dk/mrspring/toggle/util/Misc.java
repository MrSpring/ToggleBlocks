package dk.mrspring.toggle.util;

import net.minecraft.item.ItemStack;

/**
 * Created by Konrad on 05-04-2015.
 */
public class Misc
{
    public static boolean areItemStacksEqual(ItemStack stack1, ItemStack stack2, StackCompareFunction... stackCompareMethods)
    {
        for (StackCompareFunction function : stackCompareMethods)
        {
            if (!areItemStacksEqual(function, stack1, stack2))
                return false;
        }
        return true;
    }

    public static boolean areItemStacksEqual(StackCompareFunction function, ItemStack stack1, ItemStack stack2)
    {
        switch (function)
        {
            case STACK_SIZE:
                return stack1.stackSize == stack2.stackSize;
            case METADATA:
                return stack1.getItemDamage() == stack2.getItemDamage();
            case ITEM:
                return stack1.getItem() == stack2.getItem();
            case NBT:
                return ItemStack.areItemStackTagsEqual(stack1, stack2);
            default:
                return false;
        }
    }

    public enum StackCompareFunction
    {
        STACK_SIZE,
        METADATA,
        ITEM,
        NBT
    }
}
