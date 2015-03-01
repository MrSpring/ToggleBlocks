package dk.mrspring.toggle.container;

import dk.mrspring.toggle.tileentity.TileEntityToggleBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

/**
 * Created by Konrad on 27-02-2015.
 */
public class ContainerToggleBlock extends Container
{
    TileEntityToggleBlock tileEntity;

    public ContainerToggleBlock(InventoryPlayer inventoryPlayer, TileEntityToggleBlock tileEntityToggleBlock)
    {
        this.tileEntity = tileEntityToggleBlock;

        addSlotToContainer(new SingleItemSlot(this.tileEntity, 0, 8, 17));
        addSlotToContainer(new SingleItemSlot(this.tileEntity, 1, 8, 39));

        int x = 98, y=8;

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
}
