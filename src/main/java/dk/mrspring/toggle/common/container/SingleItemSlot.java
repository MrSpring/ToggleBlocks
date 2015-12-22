package dk.mrspring.toggle.common.container;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

/**
 * Created on 21-12-2015 for ToggleBlocks.
 */
public class SingleItemSlot extends Slot
{
    public SingleItemSlot(IInventory inventory, int slot, int x, int y)
    {
        super(inventory, slot, x, y);
    }

    @Override
    public int getSlotStackLimit()
    {
        return 0;
    }
}
