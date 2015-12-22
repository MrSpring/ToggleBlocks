package dk.mrspring.toggle.common.container;

import dk.mrspring.toggle.common.tileentity.InventoryChangeBlock;
import dk.mrspring.toggle.common.tileentity.TileEntityToggleBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

/**
 * Created on 21-12-2015 for ToggleBlocks.
 */
public class ContainerChangeBlock extends Container
{
    TileEntityToggleBlock tileEntity;
    InventoryChangeBlock inventory;

    public ContainerChangeBlock(InventoryPlayer player, TileEntityToggleBlock tileEntity)
    {
        this.tileEntity = tileEntity;

        inventory = tileEntity.makeChangeBlockInventoryForLastOpened();
        addSlotToContainer(new SingleItemSlot(inventory, 0, 17, 18));
        addSlotToContainer(new SingleItemSlot(inventory, 1, 17, 50));

        bindPlayerInventory(player);
    }

    public TileEntityToggleBlock getTileEntity()
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
    public boolean canInteractWith(EntityPlayer playerIn)
    {
        return true;
    }

    public InventoryChangeBlock getChangeBlock()
    {
        return inventory;
    }
}
