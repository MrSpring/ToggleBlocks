package dk.mrspring.toggle.container;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

/**
 * Created by Konrad on 01-03-2015.
 */
public class SingleItemSlot extends Slot
{
    public SingleItemSlot(IInventory p_i1824_1_, int p_i1824_2_, int p_i1824_3_, int p_i1824_4_)
    {
        super(p_i1824_1_, p_i1824_2_, p_i1824_3_, p_i1824_4_);
    }

    @Override
    public int getSlotStackLimit()
    {
        return 0;
    }
}