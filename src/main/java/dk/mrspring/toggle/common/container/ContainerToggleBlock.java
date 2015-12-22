package dk.mrspring.toggle.common.container;

import dk.mrspring.toggle.common.tileentity.TileEntityToggleBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * Created on 21-12-2015 for ToggleBlocks.
 */
public class ContainerToggleBlock extends Container
{
    TileEntityToggleBlock tileEntity;

    public ContainerToggleBlock(InventoryPlayer inventoryPlayer, TileEntityToggleBlock tileEntityToggleBlock)
    {
        this.tileEntity = tileEntityToggleBlock;

        addSlotToContainer(new SingleItemSlot(this.tileEntity, 0, 8, 17));
        addSlotToContainer(new SingleItemSlot(this.tileEntity, 1, 8, 39));

        int x = 107, y = 17;

        for (int i = 0; i < 3; i++)
        {
            for (int j = 0; j < 3; j++)
            {
                addSlotToContainer(new Slot(this.tileEntity, 2 + j + i * 3, x + j * 18, y + i * 18));
            }
        }

        bindPlayerInventory(inventoryPlayer);
    }

    public TileEntityToggleBlock getTileEntity()
    {
        return this.tileEntity;
    }

    private void bindPlayerInventory(InventoryPlayer inventoryPlayer)
    {
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 9; j++)
                addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));

        for (int i = 0; i < 9; i++)
            addSlotToContainer(new Slot(inventoryPlayer, i, 8 + i * 18, 142));
    }

    @Override
    public boolean canInteractWith(EntityPlayer player)
    {
        return true;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex)
    {
        ItemStack itemStack = null;
        Slot slot = (Slot) this.inventorySlots.get(slotIndex);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemStack1 = slot.getStack();
            itemStack = itemStack1.copy();

            if (slotIndex < 9 + 2 && slotIndex > 1)
            {
                if (!this.mergeItemStack(itemStack1, 9 + 2, this.inventorySlots.size(), true))
                    return null;
            } else if (!this.mergeItemStack(itemStack1, 2, 9 + 2, false))
                return null;

            if (itemStack1.stackSize == 0)
                slot.putStack(null);
            else slot.onSlotChanged();
        }

        return itemStack;
    }
}
