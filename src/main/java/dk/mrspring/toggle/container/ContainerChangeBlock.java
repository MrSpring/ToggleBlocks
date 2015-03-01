package dk.mrspring.toggle.container;

import dk.mrspring.toggle.tileentity.TileEntityChangeBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;

/**
 * Created by Konrad on 01-03-2015.
 */
public class ContainerChangeBlock extends Container
{
    TileEntityChangeBlock tileEntity;

    public ContainerChangeBlock(InventoryPlayer inventoryPlayer, TileEntityChangeBlock tileEntity)
    {
        this.tileEntity = tileEntity;

        addSlotToContainer(new SingleItemSlot(this.tileEntity, 0, 17, 18));
        addSlotToContainer(new SingleItemSlot(this.tileEntity, 1, 17, 50));

        bindPlayerInventory(inventoryPlayer);
    }

    public TileEntityChangeBlock getTileEntity()
    {
        return tileEntity;
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
    public boolean canInteractWith(EntityPlayer p_75145_1_)
    {
        return true;
    }
}
