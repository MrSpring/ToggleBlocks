package dk.mrspring.toggle.tileentity;

import net.minecraft.item.ItemStack;

/**
 * Created by Konrad on 15-04-2015.
 */
public class StorageStack
{
    public ItemStack stack;
    public int chestNo;
    public int slotId;

    public StorageStack(ItemStack stack, int chestNo, int slotId)
    {
        this.stack = stack;
        this.chestNo = chestNo;
        this.slotId = slotId;
    }
}
